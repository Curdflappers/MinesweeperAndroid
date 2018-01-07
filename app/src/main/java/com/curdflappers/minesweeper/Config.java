package com.curdflappers.minesweeper;

public class Config {
    private static final int MAX_ROWS = 20;
    private static final int MAX_COLS = 40;
    static int rows = 20, cols = 12, mines = 40;

    public boolean setRows(int r) {
        if(r < MAX_ROWS && r > 0
                && (cols > 1 || r > 1)
                && r * cols > mines) {
            rows = r;
            return true;
        }
        return false;
    }

    public boolean setCols(int c) {
        if(c < MAX_COLS && c > 0
                && (rows > 1 || c > 1)
                && c * rows > mines) {
            cols = c;
            return true;
        }
        return false;
    }

    public boolean setMines(int m) {
        if(m > 0 && m < rows * cols) {
            mines = m;
            return true;
        }
        return false;
    }
}
