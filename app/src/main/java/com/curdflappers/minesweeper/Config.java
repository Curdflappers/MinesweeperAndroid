package com.curdflappers.minesweeper;

import com.curdflappers.minesweeper.utils.Difficulty;

public class Config {
    private static final int MAX_ROWS = 40, MAX_COLS = 24;
    static final String INVALID_ENTRY =
            "Invalid entry, must be between 1 and %d, inclusive";

    static final Difficulty
            BEGINNER = new Difficulty(9, 9, 10),
            INTERMEDIATE = new Difficulty(16, 16, 40),
            EXPERT = new Difficulty(16, 16, 99),
            ENDURANCE = new Difficulty(40, 24, 192);

    public static final int CUSTOM = -1,
            PRESET_BEGINNER = 0,
            PRESET_INTERMEDIATE = 1,
            PRESET_EXPERT = 2,
            PRESET_ENDURANCE = 3;

    private static int rows = 20, cols = 12, mines = 40;
    static final int ROWS = 0, COLS = 1, MINES = 2;
    private static ConfigListener listener;

    static int getRows() { return rows; }
    static int getCols() { return cols; }
    static int getMines() { return mines; }

    static Difficulty getDifficulty() {
        return new Difficulty(getRows(), getCols(), getMines());
    }
    static int getPresetDifficulty() {
        Difficulty difficulty = getDifficulty();
        if (difficulty.equals(BEGINNER)) {
            return PRESET_BEGINNER;
        } else if (difficulty.equals(INTERMEDIATE)) {
            return PRESET_INTERMEDIATE;
        } else if (difficulty.equals(EXPERT)) {
            return PRESET_EXPERT;
        } else if (difficulty.equals(ENDURANCE)) {
            return PRESET_ENDURANCE;
        } else {
            return CUSTOM;
        }
    }
    static void setDifficulty(Difficulty difficulty) {
        setRows(difficulty.getRows());
        setCols(difficulty.getCols());
        setMines(difficulty.getMines());
    }

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

    interface ConfigListener {
        void rowsChanged(int rows);
        void colsChanged(int cols);
        void minesChanged(int mines);
    }
}
