package com.curdflappers.minesweeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.curdflappers.minesweeper.utils.Location;
import com.curdflappers.minesweeper.utils.MinesweeperApp;
import com.curdflappers.minesweeper.utils.VibrateService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Game implements View.OnClickListener, View.OnLongClickListener {
    private Spot[][] mSpots;
    private int mMines;
    boolean mMinefieldPopulated;
    private boolean sweepMode;
    private boolean gameOver;

    Game() {
        mSpots = new Spot[Config.rows][Config.cols];
        mMines = Config.mines;
        mMinefieldPopulated = false;
        for (int r = 0; r < mSpots.length; r++) {
            for (int c = 0; c < mSpots[r].length; c++) {
                mSpots[r][c] = new Spot(this, r, c);
            }
        }
        sweepMode = true;
        gameOver = false;
    }

    Spot[][] getSpots() {
        Spot[][] spots = new Spot[mSpots.length][mSpots[0].length];
        for(int r = 0; r < mSpots.length; r++) {
            spots[r] = mSpots[r].clone();
        }
        return spots;
    }

    @Override
    public void onClick(View view) {
        doAction(((SpotView)view).spot, sweepMode);
    }

    @Override
    public boolean onLongClick(View view) {
        if(gameOver) reset();
        else doAction(((SpotView)view).spot, !sweepMode);

        vibrate();
        return true;
    }

    private void doAction(Spot spot, boolean sweep) {
        if(gameOver) { return; }

        if(spot.getRevealed()
                && neighboringFlags(spot) >= spot.getNeighboringMines()) {
            int row = spot.getRow(), col = spot.getCol();
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if(!(r == row && c == col) && validLoc(r, c)) {
                        mSpots[r][c].sweep();
                    }
                }
            }
        }

        if(sweep) {
            if(!mMinefieldPopulated) {
                populateMinefield(spot.getRow(), spot.getCol());
            }
            spot.sweep();
        } else {
            spot.flag();
        }
    }

    private void vibrate() {
        Intent intentVibrate = new Intent(MinesweeperApp.getAppContext(),VibrateService.class);
        MinesweeperApp.getAppContext().startService(intentVibrate);
    }

    private int neighboringFlags(Spot spot) {
        int row = spot.getRow(), col = spot.getCol();
        int count = 0;

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if(!(r == row && c == col) && validLoc(r, c)
                        && mSpots[r][c].getFlagged()) {
                    count++;
                }
            }
        }
        return count;
    }

    private void populateMinefield(int row, int col) {
        // Place mines
        ArrayList<Location> locations = new ArrayList<>();
        for (int r = 0; r < mSpots.length; r++)
            for (int c = 0; c < mSpots[r].length; c++)
                if(!(r == row && c == col))
                    locations.add(new Location(r, c));

        Random random = new Random(new Date().getTime());
        for(int i = 0; i < mMines; i++)
        {
            int index = random.nextInt(locations.size());
            Location loc = locations.get(index);
            mSpots[loc.row][loc.col].setAsMine();
            locations.remove(index);
        }

        // Update neighbor count
        for (int r = 0; r < mSpots.length; r++) {
            for (int c = 0; c < mSpots[r].length; c++) {
                mSpots[r][c].populate(neighboringMines(r, c));
            }
        }

        mMinefieldPopulated = true;
    }

    private int neighboringMines(int row, int col) {
        int count = 0;

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if(!(r == row && c == col) && validLoc(r, c)
                        && mSpots[r][c].getMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean validLoc(int r, int c) {
        return r >= 0 && r < mSpots.length
                && c >=0 && c < mSpots[r].length;
    }

    void update(Spot spot) {
        if(spot.getExploded()) {
            gameOver();
            return;
        }
        if(spot.getRevealed()) {
            if(spot.getNeighboringMines() == 0) {
                int row = spot.getRow(), col = spot.getCol();
                for(int r = row - 1; r <= row + 1; r++) {
                    for(int c = col - 1; c <= col + 1; c++) {
                        if(!(r == row && c == col) && validLoc(r, c))
                            mSpots[r][c].sweep();
                    }
                }
            }
        }
    }

    private void gameOver() {
        gameOver = true;
        Toast.makeText(MinesweeperApp.getAppContext(), "Game over!", Toast.LENGTH_SHORT).show();
        for (Spot[] row : mSpots) {
            for (Spot spot : row) {
                spot.reveal();
            }
        }
    }

    void reset() {
        gameOver = false;
        mMinefieldPopulated = false;

        for (Spot[] row : mSpots) {
            for (Spot spot : row) {
                spot.reset();
            }
        }

        // Games start in sweep mode
        if(!sweepMode)
            toggleMode();
    }

    @SuppressLint("DefaultLocale")
    void toggleMode() {
        if(gameOver) { return; }
        sweepMode = !sweepMode;
    }

    public boolean getSweepMode() {
        return sweepMode;
    }
}
