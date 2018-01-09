package com.curdflappers.minesweeper.utils;

import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.curdflappers.minesweeper.Config;
import com.curdflappers.minesweeper.ConfigActivity;

import java.util.Locale;

public class ConfigEditorActionListener
        implements TextView.OnEditorActionListener {
    private final ConfigActivity mContext;
    private int mField;

    public ConfigEditorActionListener(ConfigActivity context, int field) {
        mContext = context;
        mField = field;
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        mContext.setToFullScreen();
        if(i == textView.getImeOptions() && textView.getText().length() > 0) {
            int value =
                    Integer.parseInt(textView.getText().toString());
            if (!Config.setField(mField, value)) {
                Toast.makeText(mContext, String.format(Locale.getDefault(),
                        Config.INVALID_ENTRY, Config.getMax(mField)),
                        Toast.LENGTH_SHORT).show();
                textView.setText(String.valueOf(Config.getField(mField)));
                ((EditText)textView).selectAll();
                return true;
            }
        }
        return false;
    }
}
