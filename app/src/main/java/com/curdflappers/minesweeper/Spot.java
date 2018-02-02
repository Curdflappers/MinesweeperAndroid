package com.curdflappers.minesweeper;

class Spot {
    private int mState;
    private int mRow;
    private int mCol;
    private SpotView mView;
    private SpotListener listener;
    static final int BOOL_FIELDS = 4;
    static final int MINE = 1,
        REVEALED = 2,
        FLAGGED = 4,
        EXPLODED = 8;
    static final int SWEEP_ACTION = 0,
        FLAG_ACTION = 1;

    Spot(SpotListener listener, int r, int c) {
        this.listener = listener;
        mRow = r;
        mCol = c;
    }
    boolean get(int field) { return (mState & field) > 0; }
    void set(int field, boolean val) {
        if(val) {
            mState |= field;
        } else {
            mState &= -(field + 1);
        }
    }

    int getNeighboringMines() { return (mState & 112) >> BOOL_FIELDS; }
    int getRow() { return mRow; }
    int getCol() { return mCol; }

    void setView(SpotView v) {
        mView = v;
    }

    void setNeighboringMines(int neighboringMines) {
        mState &= -113;
        mState += (neighboringMines << BOOL_FIELDS);
    }

    void sweep() {
        if(get(FLAGGED) || get(REVEALED)) { return; }

        set(REVEALED, true);
        if (get(MINE)) {
            set(EXPLODED, true);
        }

        updateState(SWEEP_ACTION);
    }

    void reveal() {
        set(REVEALED, true);
        mView.update();
    }

    void reset() {
        set(MINE, false);
        set(REVEALED, false);
        set(FLAGGED, false);
        set(EXPLODED, false);
        mView.update();
    }

    void flag() {
        if(get(REVEALED)) { return; }
        set(FLAGGED, !get(FLAGGED));
        updateState(FLAG_ACTION);
    }

    private void updateState(int action) {
        listener.spotChanged(this, action);
        mView.update();
    }

    interface SpotListener {
        void spotChanged(Spot spot, int action);
    }
}
