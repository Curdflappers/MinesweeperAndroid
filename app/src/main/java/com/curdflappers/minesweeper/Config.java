package com.curdflappers.minesweeper;

public class Config {
    public static final int MAX_ROWS = 20;
    public static final int MAX_COLS = 40;
    public static int rows = 20, cols = 10;

    public boolean setRows(int r) {
        if(r < MAX_ROWS && r > 0
                && (cols > 1 || r > 1)) {
            rows = r;
            return true;
        }
        return false;
    }

    public boolean setCols(int c) {
        if(c < MAX_COLS && c > 0
                && (rows > 1 || c > 1)) {
            cols = c;
            return true;
        }
        return false;
    }
}
