package com.curdflappers.minesweeper;

class Spot {
    private boolean mMine;
    private boolean mRevealed;
    private boolean mFlagged;
    private boolean mExploded;
    private int mNeighboringMines;
    private int mRow;
    private int mCol;
    private SpotView mView;
    private SpotListener listener;
    static final int SWEPT = 0,
        FLAGGED = 1;

    Spot(SpotListener listener, int r, int c) {
        this.listener = listener;
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

    void setAsMine() {
        mMine = true;
    }

    void setNeighboringMines(int neighboringMines) {
        mNeighboringMines = neighboringMines;
    }

    void sweep() {
        if(mFlagged || mRevealed) { return; }

        mRevealed = true;
        if (mMine) {
            mExploded = true;
        }

        updateState(SWEPT);
    }

    void reveal() {
        mRevealed = true;
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
        updateState(FLAGGED);
    }

    private void updateState(int action) {
        listener.spotChanged(this, action);
        mView.update();
    }

    interface SpotListener {
        void spotChanged(Spot spot, int action);
    }
}
