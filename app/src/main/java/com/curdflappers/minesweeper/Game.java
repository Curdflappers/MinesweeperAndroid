package com.curdflappers.minesweeper;

import android.content.Intent;
import android.view.View;

import com.curdflappers.minesweeper.utils.MinesweeperApp;
import com.curdflappers.minesweeper.utils.VibrateService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static com.curdflappers.minesweeper.Spot.EXPLODED;
import static com.curdflappers.minesweeper.Spot.FLAGGED;
import static com.curdflappers.minesweeper.Spot.MINE;
import static com.curdflappers.minesweeper.Spot.REVEALED;

public class Game implements View.OnClickListener, View.OnLongClickListener,
        Spot.SpotListener {
    private Spot[][] mSpots;
    private int mMines;
    private boolean mMinefieldPopulated;
    private boolean sweepMode;
    private boolean gameOver;
    private GameListener listener;
    private int mMinesLeft, mUnrevealedSpots;


    private static final int GAME_START = 0,
            GAME_OVER = 1,
            GAME_RESET = 2,
            MINES_LEFT = 3;
    private boolean mWin;

    private void setMinesLeft(int count) {
        mMinesLeft = count;
        notifyListener(MINES_LEFT);
    }

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
        mUnrevealedSpots = mSpots.length * mSpots[0].length;
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
        if(spot.get(REVEALED)) {
            revealNeighborsIfSafe(spot);
            return;
        }

        if(sweep) {
            if(!mMinefieldPopulated) {
                populateMinefield(spot.getRow(), spot.getCol());
            }
            spot.sweep();
        } else if (mMinesLeft > 0 || spot.get(FLAGGED)){
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
                        && mSpots[r][c].get(FLAGGED)) {
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
            spots.get(index).set(MINE, true);
            spots.remove(index);
        }

        // Update neighbor count
        for (int r = 0; r < mSpots.length; r++) {
            for (int c = 0; c < mSpots[r].length; c++) {
                mSpots[r][c].setNeighboringMines(neighboringMines(r, c));
            }
        }

        mMinefieldPopulated = true;
        notifyListener(GAME_START);
    }

    private int neighboringMines(int row, int col) {
        int count = 0;

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if(!(r == row && c == col) && validLoc(r, c)
                        && mSpots[r][c].get(MINE)) {
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
        if(spot.get(EXPLODED)) {
            gameOver(false);
            return;
        }

        switch(action) {
            case Spot.SWEEP_ACTION:
                if(spot.getNeighboringMines() == 0) {
                    int row = spot.getRow(), col = spot.getCol();
                    for(int r = row - 1; r <= row + 1; r++) {
                        for(int c = col - 1; c <= col + 1; c++) {
                            if(!(r == row && c == col) && validLoc(r, c))
                                mSpots[r][c].sweep();
                        }
                    }
                }
                mUnrevealedSpots--;
                if (mUnrevealedSpots == mMines) {
                    gameOver(true);
                }
                break;
            case Spot.FLAG_ACTION:
                boolean flagged = spot.get(FLAGGED);
                setMinesLeft(mMinesLeft + (flagged ? -1 : 1));
                break;
        }
    }

    private void gameOver(boolean win) {
        gameOver = true;
        mWin = win;
        for (Spot[] row : mSpots) {
            for (Spot spot : row) {
                if(!spot.get(REVEALED)) {
                    if(win && !spot.get(FLAGGED))
                        spot.flag();
                    else
                        spot.reveal();
                }
            }
        }
        notifyListener(GAME_OVER);
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
        notifyListener(GAME_RESET);
        setMinesLeft(mMines);
        mUnrevealedSpots = mSpots.length * mSpots[0].length;
    }

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
            case GAME_START:
                listener.gameStart();
                break;
            case GAME_OVER:
                listener.gameOver(mWin);
                break;
            case GAME_RESET:
                listener.gameReset();
                break;
            case MINES_LEFT:
                listener.minesLeftChanged(mMinesLeft);
                break;
        }
    }

    void setListener(GameListener listener) {
        this.listener = listener;
    }

    interface GameListener {
        void gameStart();
        void gameOver(boolean win);
        void gameReset();

        void minesLeftChanged(int minesLeft);
    }
}
