package com.curdflappers.minesweeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.curdflappers.minesweeper.utils.Location;
import com.curdflappers.minesweeper.utils.MinesweeperApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Game implements View.OnClickListener, View.OnLongClickListener {
    private Spot[][] mSpots;
    int mMines;
    boolean mMinefieldPopulated;
    boolean sweepMode;
    boolean gameOver;

    public Game() {
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

    public Spot[][] getSpots() {
        Spot[][] spots = new Spot[mSpots.length][mSpots[0].length];
        for(int r = 0; r < mSpots.length; r++) {
            spots[r] = mSpots[r].clone();
        }
        return spots;
    }

    @Override
    public void onClick(View view) {
        if(gameOver) { return; }
        Spot s = ((SpotView)view).spot;
        if(!mMinefieldPopulated) {
            populateMinefield(s.getRow(), s.getCol());
            mMinefieldPopulated = true;
        }
        if(sweepMode) {
            s.sweep();
        } else {
            s.flag();
        }
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

    @Override
    public boolean onLongClick(View view) {
        if(gameOver) { return true; }
        Spot s = ((SpotView)view).spot;
        if(!mMinefieldPopulated) {
            populateMinefield(s.getRow(), s.getCol());
            mMinefieldPopulated = true;
        }
        if(sweepMode) {
            s.flag();
        } else {
            s.sweep();
        }
        return true;
    }

    public void update(Spot spot) {
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

    public void reset() {
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
    public void toggleMode() {
        if(gameOver) { return; }
        sweepMode = !sweepMode;
        Toast.makeText(MinesweeperApp.getAppContext(),
                String.format("Now in %s mode", sweepMode ? "sweep" : "flag"),
                Toast.LENGTH_SHORT).show();
    }
}
