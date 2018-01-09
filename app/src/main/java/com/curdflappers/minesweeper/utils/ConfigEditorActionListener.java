package com.curdflappers.minesweeper.utils;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

public class ConfigEditorActionListener
        implements TextView.OnEditorActionListener {

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if(i == textView.getImeOptions()) {
            ConfigEditText edit = (ConfigEditText) textView;
            boolean success = edit.setField();
            if(success && i == EditorInfo.IME_ACTION_DONE)
            {
                edit.notifyListener(ConfigEditText.HIDE_KEYBOARD);
            }
            return !success; // handles failures only
        }
        return false;
    }
}
