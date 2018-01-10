package com.curdflappers.minesweeper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.curdflappers.minesweeper.Config;

public class HighScoreHelper {

    private static final String PREFS_GLOBAL = "prefs_global";
    private static final String PREF_TOP_BEGINNER = "pref_top_beginner",
            PREF_TOP_INTERMEDIATE = "pref_top_intermediate",
            PREF_TOP_EXPERT = "pref_top_expert",
            PREF_TOP_ENDURANCE = "pref_top_endurance";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_GLOBAL, Context.MODE_PRIVATE);
    }

    public static void clearTopScores(Context context) {
        clearTopScore(context, Config.PRESET_BEGINNER,
                getPref(Config.PRESET_BEGINNER));
        clearTopScore(context, Config.PRESET_INTERMEDIATE,
                getPref(Config.PRESET_INTERMEDIATE));
        clearTopScore(context, Config.PRESET_EXPERT,
                getPref(Config.PRESET_EXPERT));
        clearTopScore(context, Config.PRESET_ENDURANCE,
                getPref(Config.PRESET_ENDURANCE));
    }

    private static void clearTopScore(Context context, int diff, String pref) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        if(getTopScore(context, diff) == 0) {
            editor.putInt(pref, Integer.MAX_VALUE);
        }
        editor.apply();
    }

    private static String getPref(int diff) {
        switch (diff) {
            case Config.PRESET_BEGINNER:
                return PREF_TOP_BEGINNER;
            case Config.PRESET_INTERMEDIATE:
                return PREF_TOP_INTERMEDIATE;
            case Config.PRESET_EXPERT:
                return PREF_TOP_EXPERT;
            case Config.PRESET_ENDURANCE:
                return PREF_TOP_ENDURANCE;
            default:
                return null;
        }
    }

    //  Setters and getters for global preferences
    public static boolean isTopScore(Context context, int newScore, int diff) {
        if(newScore < 0) { return false; }
        String pref = getPref(diff);
        return newScore < getPreferences(context).getInt(pref, 0);
    }

    private static int getTopScore(Context context, int diffCode) {
        return getPreferences(context).getInt(getPref(diffCode), 0);
    }

    public static void setTopScore(Context context, int score, int diffCode) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        String pref = getPref(diffCode);
        editor.putInt(pref, score);
        editor.apply();
    }

}
