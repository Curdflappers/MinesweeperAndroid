package com.curdflappers.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        setToFullScreen();
        findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConfigActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        final EditText rowsEdit = findViewById(R.id.rows_edit);
        rowsEdit.setText(String.valueOf(Config.rows));
        rowsEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    int newRows =
                            Integer.parseInt(rowsEdit.getText().toString());
                    boolean success = Config.setRows(newRows);
                    if(!success) {
                        Toast.makeText(ConfigActivity.this,
                                String.format(Locale.getDefault(), Config.INVALID_ENTRY, Config.MAX_ROWS),
                                Toast.LENGTH_SHORT).show();
                        rowsEdit.setText(String.valueOf(Config.rows));
                    }
                    ConfigActivity.this.setToFullScreen();
                }
            }
        });

        final EditText colsEdit = findViewById(R.id.columns_edit);
        colsEdit.setText(String.valueOf(Config.cols));
        colsEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    int newCols =
                            Integer.parseInt(colsEdit.getText().toString());
                    boolean success = Config.setCols(newCols);
                    if(!success) {
                        Toast.makeText(ConfigActivity.this,
                                String.format(Locale.getDefault(), Config.INVALID_ENTRY, Config.MAX_COLS),
                                Toast.LENGTH_SHORT).show();
                        colsEdit.setText(String.valueOf(Config.cols));
                    }
                    ConfigActivity.this.setToFullScreen();
                }
            }
        });

        final EditText minesEdit = findViewById(R.id.mines_edit);
        minesEdit.setText(String.valueOf(Config.mines));
        minesEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    int newMines =
                            Integer.parseInt(minesEdit.getText().toString());
                    boolean success = Config.setMines(newMines);
                    if(!success) {
                        Toast.makeText(ConfigActivity.this,
                                String.format(Locale.getDefault(), Config.INVALID_ENTRY, Config.maxMines()),
                                Toast.LENGTH_SHORT).show();
                        minesEdit.setText(String.valueOf(Config.mines));
                    }
                    ConfigActivity.this.setToFullScreen();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    private void setToFullScreen()
    {
        findViewById(R.id.activity_config).setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
