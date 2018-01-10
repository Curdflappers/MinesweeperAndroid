package com.curdflappers.minesweeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.curdflappers.minesweeper.utils.MinesweeperApp;
import com.curdflappers.minesweeper.utils.VibrateService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Game implements View.OnClickListener, View.OnLongClickListener,
        Spot.SpotListener {
    private Spot[][] mSpots;
    private int mMines;
    private boolean mMinefieldPopulated;
    private boolean sweepMode;
    private boolean gameOver;
    private GameListener listener;
    private int mMinesLeft;

    private void setMinesLeft(int count) {
        mMinesLeft = count;
        notifyListener(MINES_LEFT);
    }

    private static final int TIMER_START = 0,
            TIMER_STOP = 1,
            TIMER_RESET = 2,
            MINES_LEFT = 3;

    Game(GameListener listener) {
        mSpots = new Spot[Config.getRows()][Config.getCols()];
        mMines = Config.getMines();
        mMinefieldPopulated = false;
        for (int r = 0; r < mSpots.length; r++) {
            for (int c = 0; c < mSpots[r].length; c++) {
                mSpots[r][c] = new Spot(this, r, c);
            }
        }
        sweepMode = true;
        gameOver = false;
        this.listener = listener;
        setMinesLeft(Config.getMines());
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
        if(gameOver) return;

        // tapping revealed spots reveals neighbors or does nothing
        if(spot.getRevealed()) {
            revealNeighborsIfSafe(spot);
            return;
        }

        if(sweep) {
            if(!mMinefieldPopulated) {
                populateMinefield(spot.getRow(), spot.getCol());
            }
            spot.sweep();
        } else if (mMinesLeft > 0 || spot.getFlagged()){
            spot.flag();
        }
    }

    private void revealNeighborsIfSafe(Spot spot) {
        if(neighboringFlags(spot) >= spot.getNeighboringMines()) {
            int row = spot.getRow(), col = spot.getCol();
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if(!(r == row && c == col) && validLoc(r, c)) {
                        mSpots[r][c].sweep();
                    }
                }
            }
        }
    }

    private void vibrate() {
        Intent intentVibrate =
                new Intent(MinesweeperApp.getAppContext(),VibrateService.class);
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
        // Get list of viable mine spots (all except pressed spot)
        ArrayList<Spot> spots = new ArrayList<>();
        for (int r = 0; r < mSpots.length; r++)
            for (int c = 0; c < mSpots[r].length; c++)
                if(!(r == row && c == col))
                    spots.add(mSpots[r][c]);

        // Place mines
        Random random = new Random(new Date().getTime());
        for(int i = 0; i < mMines; i++)
        {
            int index = random.nextInt(spots.size());
            spots.get(index).setAsMine();
            spots.remove(index);
        }

        // Update neighbor count
        for (int r = 0; r < mSpots.length; r++) {
            for (int c = 0; c < mSpots[r].length; c++) {
                mSpots[r][c].setNeighboringMines(neighboringMines(r, c));
            }
        }

        mMinefieldPopulated = true;
        notifyListener(TIMER_START);
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

    public void spotChanged(Spot spot, int action) {
        if(spot.getExploded()) {
            gameOver();
            return;
        }

        switch(action) {
            case Spot.SWEPT:
                if(spot.getNeighboringMines() == 0) {
                    int row = spot.getRow(), col = spot.getCol();
                    for(int r = row - 1; r <= row + 1; r++) {
                        for(int c = col - 1; c <= col + 1; c++) {
                            if(!(r == row && c == col) && validLoc(r, c))
                                mSpots[r][c].sweep();
                        }
                    }
                }
                break;
            case Spot.FLAGGED:
                boolean flagged = spot.getFlagged();
                setMinesLeft(mMinesLeft + (flagged ? -1 : 1));
                break;
        }
    }

    private void gameOver() {
        gameOver = true;
        Toast.makeText(MinesweeperApp.getAppContext(), "Game over!",
                Toast.LENGTH_SHORT).show();
        for (Spot[] row : mSpots) {
            for (Spot spot : row) {
                spot.reveal();
            }
        }
        notifyListener(TIMER_STOP);
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
        notifyListener(TIMER_RESET);
        setMinesLeft(mMines);
    }

    @SuppressLint("DefaultLocale")
    void toggleMode() {
        if(gameOver) { return; }
        sweepMode = !sweepMode;
    }

    boolean getSweepMode() {
        return sweepMode;
    }

    private void notifyListener(int eventId) {
        if (listener == null) return;
        switch (eventId) {
            case TIMER_START:
                listener.startTimer();
                break;
            case TIMER_STOP:
                listener.stopTimer();
                break;
            case TIMER_RESET:
                listener.resetTimer();
                break;
            case MINES_LEFT:
                listener.minesLeftChanged(mMinesLeft);
        }
    }

    interface GameListener {
        void startTimer();
        void stopTimer();
        void resetTimer();

        void minesLeftChanged(int minesLeft);
    }
}
