package com.curdflappers.minesweeper.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MinesweeperApp extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MinesweeperApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MinesweeperApp.context;
    }
}
