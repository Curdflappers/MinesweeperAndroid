package com.curdflappers.minesweeper.utils;

import android.view.View;
public class ConfigFocusChangeListener implements View.OnFocusChangeListener {
    @Override
    public void onFocusChange(View view, boolean b) {
        if(!b) {
            ((ConfigEditText)view).setField();
        }
    }
}
