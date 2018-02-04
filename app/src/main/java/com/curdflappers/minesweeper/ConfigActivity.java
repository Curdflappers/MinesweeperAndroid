package com.curdflappers.minesweeper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.curdflappers.minesweeper.utils.ConfigEditText;
import com.curdflappers.minesweeper.utils.Difficulty;
import com.curdflappers.minesweeper.utils.SoundHelper;

import java.util.Locale;

public class ConfigActivity extends AppCompatActivity
        implements Config.ConfigListener,
        ConfigEditText.ConfigEditTextListener {

    private ConfigEditText rowsEdit, colsEdit, minesEdit;
    private Difficulty prevDifficulty;
    private View mPlayButton, mActivity;
    private static final float SIDE_PADDING_PCT = 0.2f,
            TOP_PADDING_PCT = 0.05f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        setToFullScreen();
        mActivity = findViewById(R.id.activity_config);
        mPlayButton = findViewById(R.id.play_button);
        prevDifficulty = Config.getDifficulty();

        setEdits();
        setPresetButtons();

        Config.setListener(this);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Config.getDifficulty().equals(prevDifficulty)) {
                    GameActivity.reset(); // wipe out the old game
                }
                Intent i = new Intent(ConfigActivity.this, GameActivity.class);
                startActivity(i);
            }
        });
        mActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.requestFocus();
            }
        });

        mActivity.requestFocus();

        setPadding();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSeekBars();
    }

    private void setSeekBars() {
        SoundHelper soundHelper = GameActivity.mSoundHelper;
        SeekBar musicBar = findViewById(R.id.music_seekbar);
        SeekBar sfxBar = findViewById(R.id.sfx_seekbar);

        musicBar.setProgress(
                (int) (soundHelper.getMusicVolume() * musicBar.getMax()));
        sfxBar.setProgress(
                (int) (soundHelper.getSFXVolume() * sfxBar.getMax()));

        musicBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        float percent = (float) (i) / seekBar.getMax();
                        GameActivity.mSoundHelper.setMusicVolume(percent);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
        sfxBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        float percent = (float) (i) / seekBar.getMax();
                        GameActivity.mSoundHelper.setSFXVolume(percent);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
    }

    private void setEdits() {
        rowsEdit = findViewById(R.id.rows_edit);
        rowsEdit.setText(String.valueOf(Config.getLonger()));
        setListeners(rowsEdit, Config.LONGER);

        colsEdit = findViewById(R.id.columns_edit);
        colsEdit.setText(String.valueOf(Config.getShorter()));
        setListeners(colsEdit, Config.SHORTER);

        minesEdit = findViewById(R.id.mines_edit);
        minesEdit.setText(String.valueOf(Config.getMines()));
        setListeners(minesEdit, Config.MINES);
    }

    private void setPresetButtons() {
        setPresetListener(findViewById(R.id.beginner_button), Config.BEGINNER);
        setPresetListener(
                findViewById(R.id.intermediate_button), Config.INTERMEDIATE);
        setPresetListener(findViewById(R.id.expert_button), Config.EXPERT);
        setPresetListener(
                findViewById(R.id.endurance_button), Config.ENDURANCE);
    }

    private void setPresetListener(View view, final Difficulty difficulty) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.setDifficulty(difficulty);
                mActivity.requestFocus();
            }
        });
    }

    private void setPadding() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int paddingSide = (int) (SIDE_PADDING_PCT * width);
        int paddingTop = (int) (TOP_PADDING_PCT * height);
        mActivity.setPadding(paddingSide, paddingTop, paddingSide, 0);
    }

    private void setListeners(ConfigEditText edit, int field) {
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i,
                                          KeyEvent keyEvent) {
                if (i == textView.getImeOptions()) {
                    ConfigEditText edit = (ConfigEditText) textView;
                    boolean success = edit.setField();
                    if (success && i == EditorInfo.IME_ACTION_DONE) {
                        edit.notifyListener(ConfigEditText.HIDE_KEYBOARD);
                        edit.clearFocus();
                    }
                    return !success; // handles failures only
                }
                return false;
            }
        });
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    ConfigEditText edit = (ConfigEditText) view;
                    edit.setField();
                    edit.notifyListener(ConfigEditText.HIDE_KEYBOARD);
                }
            }
        });
        edit.setConfigBackListener(this);
        edit.setField(field);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    private void setToFullScreen() {
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
    public void hideKeyboard(ConfigEditText edit) {
        if (edit == null) return;
        edit.setText(String.valueOf(Config.getField(edit.getField())));
        mActivity.requestFocus();

        InputMethodManager mgr = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr != null) mgr.hideSoftInputFromWindow(edit.getWindowToken(), 0);

        setToFullScreen();
    }

    @Override
    public void setFieldFailure(ConfigEditText edit) {
        Toast.makeText(this, String.format(Locale.getDefault(),
                Config.INVALID_ENTRY, Config.getMax(edit.getField())),
                Toast.LENGTH_LONG).show();
        edit.setText(String.valueOf(Config.getField(edit.getField())));
        edit.selectAll();
    }
}
