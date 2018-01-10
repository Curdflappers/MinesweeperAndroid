package com.curdflappers.minesweeper;

public class Config {
    private static final int MAX_ROWS = 40, MAX_COLS = 24;
    static final String INVALID_ENTRY =
            "Invalid entry, must be between 1 and %d, inclusive";
    static final int BEGINNER = 0,
        INTERMEDIATE = 1,
        EXPERT = 2,
        ENDURANCE = 3;

    private static int rows = 20, cols = 12, mines = 40;
    static final int ROWS = 0, COLS = 1, MINES = 2;
    private static ConfigListener listener;

    static int getRows() { return rows; }
    static int getCols() { return cols; }
    static int getMines() { return mines; }

    private static boolean setRows(int r) {
        if(r <= MAX_ROWS && r > 0
                && (cols > 1 || r > 1)) {
            rows = r;
            setMines(Math.min(mines, maxMines()));
            notifyListener(ROWS, rows);
            return true;
        }
        return false;
    }

    private static boolean setCols(int c) {
        if(c <= MAX_COLS && c > 0
                && (rows > 1 || c > 1)) {
            cols = c;
            setMines(Math.min(mines, maxMines()));
            notifyListener(COLS, cols);
            return true;
        }
        return false;
    }

    private static boolean setMines(int m) {
        if(m > 0 && m <= maxMines()) {
            mines = m;
            notifyListener(MINES, mines);
            return true;
        }
        return false;
    }

    private static int maxMines() {
        return rows * cols - 1;
    }

    static void setListener(ConfigListener l) { listener = l; }

    private static void notifyListener(int field, int value) {
        if (listener == null) return;
        switch(field) {
            case ROWS:
                listener.rowsChanged(value);
                break;
            case COLS:
                listener.colsChanged(value);
                break;
            case MINES:
                listener.minesChanged(value);
                break;
        }
    }

    static int getField(int field) {
        switch(field) {
            case ROWS:
                return getRows();
            case COLS:
                return getCols();
            case MINES:
                return getMines();
        }
        return -1;
    }

    public static boolean setField(int field, int value) {
        switch(field) {
            case Config.ROWS:
                return Config.setRows(value);
            case Config.COLS:
                return Config.setCols(value);
            case Config.MINES:
                return Config.setMines(value);
        }
        return false;
    }

    static int getMax(int field) {
        switch (field) {
            case Config.ROWS:
                return Config.MAX_ROWS;
            case Config.COLS:
                return Config.MAX_COLS;
            case Config.MINES:
                return Config.maxMines();
        }
        return -1;
    }

    static void setDifficulty(int difficulty) {
        switch(difficulty) {
            case BEGINNER:
                setRows(9);
                setCols(9);
                setMines(10);
                break;
            case INTERMEDIATE:
                setRows(16);
                setCols(16);
                setMines(40);
                break;
            case EXPERT:
                setRows(16);
                setCols(30);
                setMines(99);
                break;
            case ENDURANCE:
                setRows(40);
                setCols(24);
                setMines(192);
                break;
        }
    }

    interface ConfigListener {
        void rowsChanged(int rows);
        void colsChanged(int cols);
        void minesChanged(int mines);
    }
}
