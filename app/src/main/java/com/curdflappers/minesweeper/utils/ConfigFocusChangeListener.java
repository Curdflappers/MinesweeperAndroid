package com.curdflappers.minesweeper.utils;

import android.view.View;
import android.widget.TextView;

import com.curdflappers.minesweeper.Config;

public class ConfigFocusChangeListener implements View.OnFocusChangeListener {
    private final int mField;

    public ConfigFocusChangeListener(int field) {
        mField = field;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(!b) {
            ((TextView)view).setText(String.valueOf(Config.getField(mField)));
        }
    }
}
