package com.curdflappers.minesweeper;

class Config {
    static final int MAX_ROWS = 20;
    static final int MAX_COLS = 40;
    static final String INVALID_ENTRY = "Invalid entry, must be between 1 and %d, inclusive";
    static int rows = 20, cols = 12, mines = 40;

    static boolean setRows(int r) {
        if(r <= MAX_ROWS && r > 0
                && (cols > 1 || r > 1)) {
            rows = r;
            mines = Math.min(mines, maxMines());
            return true;
        }
        return false;
    }

    static boolean setCols(int c) {
        if(c <= MAX_COLS && c > 0
                && (rows > 1 || c > 1)) {
            cols = c;
            mines = Math.min(mines, maxMines());
            return true;
        }
        return false;
    }

    static boolean setMines(int m) {
        if(m > 0 && m <= maxMines()) {
            mines = m;
            return true;
        }
        return false;
    }

    static int maxMines() {
        return rows * cols - 1;
    }
}
