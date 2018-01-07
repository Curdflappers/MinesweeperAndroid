package com.curdflappers.minesweeper;

public class Spot {
    private boolean mMine;
    private boolean mRevealed;
    private boolean mFlagged;
    private boolean mExploded;
    private int mNeighboringMines;
    private int mRow;
    private int mCol;
    private Game mGame;
    private SpotView mView;

    public Spot(Game game, int r, int c) {
        mGame = game;
        mRow = r;
        mCol = c;
    }

    public boolean getMine() { return mMine; }
    public boolean getRevealed() { return mRevealed; }
    public boolean getFlagged() { return mFlagged; }
    public boolean getExploded() { return mExploded; }
    public int getNeighboringMines() { return mNeighboringMines; }
    public int getRow() { return mRow; }
    public int getCol() { return mCol; }

    public void setView(SpotView v) {
        mView = v;
    }

    /**
     * Can only become a mine before the field is fully populated
     * @return
     */
    public boolean setAsMine() {
        if(mGame.mMinefieldPopulated) { return false; }
        mMine = true;
        return true;
    }

    public void populate(int neighboringMines) {
        mNeighboringMines = neighboringMines;
    }

    public void sweep() {
        if(mFlagged || mRevealed) { return; }

        mRevealed = true;
        if (mMine) {
            mExploded = true;
        }

        updateState();
    }

    public void reveal() {
        mRevealed = true;
        mView.update();
        // do not update game logic here
    }

    private void updateState() {
        mGame.update(this);
        mView.update();
    }

    public void reset() {
        mFlagged = false;
        mRevealed = false;
        mExploded = false;
        mMine = false;
        mView.update();
    }

    public void flag() {
        if(mRevealed) { return; }
        mFlagged = !mFlagged;
        updateState();
    }
}
