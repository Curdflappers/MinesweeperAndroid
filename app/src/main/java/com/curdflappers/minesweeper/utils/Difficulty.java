package com.curdflappers.minesweeper.utils;

public class Difficulty {
    private int longer, shorter, mines;

    public Difficulty(int l, int s, int m) {
        if(l >= s) {
            longer = l;
            shorter = s;
        }  else {
            longer = s;
            shorter = l;
        }
        mines = m;
    }

    public int getLonger() {
        return longer;
    }

    public int getShorter() {
        return shorter;
    }

    public int getMines() {
        return mines;
    }

    @Override
    public boolean equals(Object other) {
        if(other.getClass() == Difficulty.class) {
            Difficulty d = (Difficulty)other;
            return longer == d.getLonger()
                    && shorter == d.getShorter()
                    && mines == d.getMines();
        }
        return false;
    }
}
