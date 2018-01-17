package com.curdflappers.minesweeper.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.curdflappers.minesweeper.Config;

public class ConfigEditText
        extends android.support.v7.widget.AppCompatEditText {
    private ConfigEditTextListener mListener;
    private int mField;
    public static final int HIDE_KEYBOARD = 0;
    private static final int SET_FIELD_FAILURE = 1;

    public ConfigEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setConfigBackListener(ConfigEditTextListener listener) {
        mListener = listener;
    }

    public void setField(int field) {
        mField = field;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            notifyListener(HIDE_KEYBOARD);
        }
        return false;
    }

    public void notifyListener(int eventId) {
        if (mListener == null) return;
        switch(eventId) {
            case HIDE_KEYBOARD:
                mListener.hideKeyboard(this);
                break;
            case SET_FIELD_FAILURE:
                mListener.setFieldFailure(this);
                break;
        }
    }

    public int getField() {
        return mField;
    }

    public boolean setField() {
        String text = getText().toString();
        boolean success = text.length() != 0
                && Config.setField(getField(), Integer.parseInt(text));
        if(!success) notifyListener(SET_FIELD_FAILURE);
        return success;
    }

    public interface ConfigEditTextListener {
        void hideKeyboard(ConfigEditText edit);
        void setFieldFailure(ConfigEditText edit);
    }
}
