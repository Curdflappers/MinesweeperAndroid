package com.curdflappers.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.curdflappers.minesweeper.utils.ConfigEditText;
import com.curdflappers.minesweeper.utils.ConfigEditorActionListener;
import com.curdflappers.minesweeper.utils.ConfigFocusChangeListener;

public class ConfigActivity extends AppCompatActivity
        implements Config.ConfigListener, ConfigEditText.ConfigBackListener {

    ConfigEditText rowsEdit, colsEdit, minesEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        setToFullScreen();
        findViewById(R.id.play_button).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConfigActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        Config.setListener(this);

        rowsEdit = findViewById(R.id.rows_edit);
        rowsEdit.setText(String.valueOf(Config.getRows()));
        setListeners(rowsEdit, Config.ROWS);

        colsEdit = findViewById(R.id.columns_edit);
        colsEdit.setText(String.valueOf(Config.getCols()));
        setListeners(colsEdit, Config.COLS);

        minesEdit = findViewById(R.id.mines_edit);
        minesEdit.setText(String.valueOf(Config.getMines()));
        setListeners(minesEdit, Config.MINES);
    }

    private void setListeners(ConfigEditText edit, int field) {
        edit.setOnEditorActionListener(
                new ConfigEditorActionListener(this, field));
        edit.setOnFocusChangeListener(
                new ConfigFocusChangeListener(field));
        edit.setConfigBackListener(this);
        edit.setField(field);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    public void setToFullScreen()
    {
        findViewById(R.id.activity_config).setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void rowsChanged(int rows) {
        rowsEdit.setText(String.valueOf(rows));
    }

    @Override
    public void colsChanged(int cols) {
        colsEdit.setText(String.valueOf(cols));
    }

    @Override
    public void minesChanged(int mines) {
        minesEdit.setText(String.valueOf(mines));
    }

    @Override
    public void backButtonPressed(ConfigEditText edit) {
        edit.setText(String.valueOf(Config.getField(edit.getField())));
        setToFullScreen();
    }
}
