package com.curdflappers.minesweeper;

import com.curdflappers.minesweeper.utils.Difficulty;

public class Config {
    private static final int MAX_LONGER = 40, MAX_SHORTER = 24;
    static final String INVALID_ENTRY =
            "Invalid entry, must be between 1 and %d, inclusive";

    static final Difficulty
            BEGINNER = new Difficulty(9, 9, 10),
            INTERMEDIATE = new Difficulty(16, 16, 40),
            EXPERT = new Difficulty(16, 16, 99),
            ENDURANCE = new Difficulty(MAX_LONGER, MAX_SHORTER, 192);

    @SuppressWarnings("WeakerAccess")
    public static final int CUSTOM = -1,
            PRESET_BEGINNER = 0,
            PRESET_INTERMEDIATE = 1,
            PRESET_EXPERT = 2,
            PRESET_ENDURANCE = 3;

    private static int longer = ENDURANCE.getLonger(),
            shorter = ENDURANCE.getShorter(),
            mines = ENDURANCE.getMines();
    static final int LONGER = 0, SHORTER = 1, MINES = 2;
    private static ConfigListener listener;

    static int getLonger() {
        return longer;
    }

    static int getShorter() {
        return shorter;
    }

    static int getMines() {
        return mines;
    }

    static Difficulty getDifficulty() {
        return new Difficulty(getLonger(), getShorter(), getMines());
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
        setLonger(difficulty.getLonger());
        setShorter(difficulty.getShorter());
        setMines(difficulty.getMines());
    }

    private static boolean setLonger(int l) {
        if (l > MAX_LONGER) return false;

        if (l < shorter) {
            setLonger(shorter);
            setShorter(l);
        } if (l > 0 && (shorter > 1 || l > 1)) { // can't be 1x1
            longer = l;
            setMines(Math.min(mines, maxMines()));
            notifyListener(LONGER, longer);
            return true;
        }
        return false;
    }

    private static boolean setShorter(int s) {
        if (s > MAX_SHORTER) return false;

        if (s > longer) {
            setShorter(longer);
            setLonger(s);
        } if (s > 0 && (longer > 1 || s > 1)) {
            shorter = s;
            setMines(Math.min(mines, maxMines()));
            notifyListener(SHORTER, shorter);
            return true;
        }
        return false;
    }

    private static boolean setMines(int m) {
        if (m > 0 && m <= maxMines()) {
            mines = m;
            notifyListener(MINES, mines);
            return true;
        }
        return false;
    }

    private static int maxMines() {
        return longer * shorter - 1;
    }

    static void setListener(ConfigListener l) {
        listener = l;
    }

    private static void notifyListener(int field, int value) {
        if (listener == null) return;
        switch (field) {
            case LONGER:
                listener.rowsChanged(value);
                break;
            case SHORTER:
                listener.colsChanged(value);
                break;
            case MINES:
                listener.minesChanged(value);
                break;
        }
    }

    static int getField(int field) {
        switch (field) {
            case LONGER:
                return getLonger();
            case SHORTER:
                return getShorter();
            case MINES:
                return getMines();
        }
        return -1;
    }

    public static boolean setField(int field, int value) {
        switch (field) {
            case Config.LONGER:
                return Config.setLonger(value);
            case Config.SHORTER:
                return Config.setShorter(value);
            case Config.MINES:
                return Config.setMines(value);
        }
        return false;
    }

    static int getMax(int field) {
        switch (field) {
            case Config.LONGER:
                return Config.MAX_LONGER;
            case Config.SHORTER:
                return Config.MAX_SHORTER;
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
