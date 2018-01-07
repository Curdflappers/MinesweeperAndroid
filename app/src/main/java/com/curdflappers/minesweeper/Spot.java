package com.curdflappers.minesweeper;

class Spot {
    private boolean mMine;
    private boolean mRevealed;
    private boolean mFlagged;
    private boolean mExploded;
    private int mNeighboringMines;
    private int mRow;
    private int mCol;
    private Game mGame;
    private SpotView mView;

    Spot(Game game, int r, int c) {
        mGame = game;
        mRow = r;
        mCol = c;
    }

    boolean getMine() { return mMine; }
    boolean getRevealed() { return mRevealed; }
    boolean getFlagged() { return mFlagged; }
    boolean getExploded() { return mExploded; }
    int getNeighboringMines() { return mNeighboringMines; }
    int getRow() { return mRow; }
    int getCol() { return mCol; }

    void setView(SpotView v) {
        mView = v;
    }

    /**
     * Can only become a mine before the field is fully populated
     */
    void setAsMine() {
        if(mGame.mMinefieldPopulated) { return; }
        mMine = true;
    }

    void populate(int neighboringMines) {
        mNeighboringMines = neighboringMines;
    }

    void sweep() {
        if(mFlagged || mRevealed) { return; }

        mRevealed = true;
        if (mMine) {
            mExploded = true;
        }

        updateState();
    }

    void reveal() {
        mRevealed = true;
        mView.update();
        // do not update game logic here
    }

    private void updateState() {
        mGame.update(this);
        mView.update();
    }

    void reset() {
        mFlagged = false;
        mRevealed = false;
        mExploded = false;
        mMine = false;
        mView.update();
    }

    void flag() {
        if(mRevealed) { return; }
        mFlagged = !mFlagged;
        updateState();
    }
}
