package com.curdflappers.minesweeper.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class ConfigEditText
        extends android.support.v7.widget.AppCompatEditText {
    ConfigBackListener mListener;
    int mField;

    public ConfigEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setConfigBackListener(ConfigBackListener listener) {
        mListener = listener;
    }

    public void setField(int field) {
        mField = field;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mListener != null) { mListener.backButtonPressed(this); }
        }
        return false;
    }

    public int getField() {
        return mField;
    }

    public interface ConfigBackListener {
        void backButtonPressed(ConfigEditText edit);
    }
}
