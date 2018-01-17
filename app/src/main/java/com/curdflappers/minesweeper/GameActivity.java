package com.curdflappers.minesweeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.curdflappers.minesweeper.utils.HighScoreHelper;
import com.curdflappers.minesweeper.utils.SimpleAlertDialog;
import com.curdflappers.minesweeper.utils.SoundHelper;

import java.util.Locale;

public class GameActivity extends AppCompatActivity
        implements Game.GameListener {

    RelativeLayout minefield;
    private int minefieldWidth, minefieldHeight;
    Game game;
    Handler mHandler;
    int mInterval = 250; // time delay to update timer (too long makes it skip)
    long mStartTime = 0L;
    private TextView mTimerView, mMinesLeftView;
    private ModeButtonView mModeButton;
    public static SoundHelper mSoundHelper;
    private boolean mGamePlaying;
    Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mStartTime == 0L) {
                mStartTime = System.currentTimeMillis();
            }
            try {
                updateTimer();
            } finally {
                mHandler.postDelayed(mTimerRunnable, mInterval);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setToFullScreen();
        HighScoreHelper.clearTopScores(this);

        minefield = findViewById(R.id.minefield);
        mHandler = new Handler();
        mTimerView = findViewById(R.id.timer_view);
        mMinesLeftView = findViewById(R.id.mines_left_view);
        game = new Game(this);
        if(mSoundHelper == null) {
            mSoundHelper = new SoundHelper(this);
            mSoundHelper.prepareMusicPlayer(this);
        }


        findViewById(R.id.reset_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        game.reset();
                    }
                });


        mModeButton = findViewById(R.id.mode_button);
        mModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.toggleMode();
                if (game.getSweepMode())
                    mModeButton.setImageResource(R.drawable.mine_icon);
                else mModeButton.setImageResource(R.drawable.flag_icon);
            }
        });

        findViewById(R.id.config_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(
                                GameActivity.this, ConfigActivity.class);
                        startActivity(i);
                    }
                });

        ViewTreeObserver viewTreeObserver = minefield.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            minefield.getViewTreeObserver().
                                    removeOnGlobalLayoutListener(this);
                            minefieldWidth = minefield.getWidth();
                            minefieldHeight = minefield.getHeight();
                            showMineField();
                        }
                    });
        }

        findViewById(R.id.activity_game).setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                    setToFullScreen();
            }
        });

        mGamePlaying = false;
    }

    void updateTimer() {
        long millisElapsed = (int) (System.currentTimeMillis() - mStartTime);
        mTimerView.setText(timeFormat((int)millisElapsed/1000));
    }

    private void connectSpot(SpotView view, int row, int col) {
        Spot spot = game.getSpots()[row][col];
        view.spot = spot;
        spot.setView(view);
        view.setOnClickListener(game);
        view.setOnLongClickListener(game);
    }

    private void showMineField() {
        int x = 0, y = 0, offsetX = 0, offsetY = 0;
        int rows = Config.getRows(), cols = Config.getCols();

        // Set up visual formatting
        int sideLength =
                Math.min(minefieldWidth / cols, minefieldHeight / rows);
        if (sideLength < minefieldWidth / cols) { // horizontal offset
            offsetX = (minefieldWidth - sideLength * cols) / 2;
        } else { // vertical offset
            offsetY = (minefieldHeight - sideLength * rows) / 2;
        }

        // Place the spot views
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                SpotView spotView = new SpotView(
                        this, sideLength, x + offsetX, y + offsetY);
                minefield.addView(spotView);
                connectSpot(spotView, r, c);
                x += sideLength;
            }
            x = 0;
            y += sideLength;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundHelper.pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
        if(mGamePlaying) mSoundHelper.playMusic();
    }

    @Override
    public void onDestroy() {
        stopTimer();
        super.onDestroy();
    }

    private void setToFullScreen() {
        ViewGroup rootLayout = findViewById(R.id.activity_game);
        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void gameStart() {
        mGamePlaying = true;
        startTimer();
        mSoundHelper.playMusic();
    }

    @Override
    public void gameOver(boolean win) {
        mGamePlaying = false;
        stopTimer();
        mSoundHelper.pauseMusic();
        int score = (int)((System.currentTimeMillis() - mStartTime) / 1000);
        if(win) {
            Toast.makeText(this, "You win!",
                    Toast.LENGTH_SHORT).show();
            int diffCode = Config.getPresetDifficulty();
            if (HighScoreHelper.isTopScore(this, score, diffCode)) {
                HighScoreHelper.setTopScore(this, score, diffCode);

                SimpleAlertDialog dialog = SimpleAlertDialog.newInstance(
                        "New High Score!", String.format(
                                Locale.getDefault(),
                                "Your new high score is %s!",
                                timeFormat(score)));
                dialog.show(getSupportFragmentManager(), null);
                setToFullScreen();
            }
        } else {
            mSoundHelper.playSound();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void gameReset() {
        mGamePlaying = false;
        mModeButton.setImageResource(R.drawable.mine_icon);
        stopTimer();
        mSoundHelper.pauseMusic();
        mSoundHelper.resetMusic();
        mStartTime = 0L;
        mTimerView.setText("00:00");
    }

    @Override
    public void minesLeftChanged(int minesLeft) {
        mMinesLeftView.setText(String.format(
                Locale.getDefault(), "%03d", minesLeft));
    }

    private String timeFormat(int seconds) {
        int minutes = seconds/60;
        seconds %= 60;
        return String.format(Locale.getDefault(),
                "%02d:%02d", minutes, seconds);
    }

    private void startTimer() {
        mTimerRunnable.run();
    }

    private void stopTimer() {
        mHandler.removeCallbacks(mTimerRunnable);
    }
}
