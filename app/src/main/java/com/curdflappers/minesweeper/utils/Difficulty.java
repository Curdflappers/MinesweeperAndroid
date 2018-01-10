package com.curdflappers.minesweeper.utils;

public class Difficulty {
    private int rows, cols, mines;

    public Difficulty(int r, int c, int m) {
        rows = r;
        cols = c;
        mines = m;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMines() {
        return mines;
    }

    @Override
    public boolean equals(Object other) {
        if(other.getClass() == Difficulty.class) {
            Difficulty d = (Difficulty)other;
            return rows == d.getRows()
                    && cols == d.getCols()
                    && mines == d.getMines();
        }
        return false;
    }
}
