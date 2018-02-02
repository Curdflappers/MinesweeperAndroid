package com.curdflappers.minesweeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.curdflappers.minesweeper.utils.HighScoreHelper;
import com.curdflappers.minesweeper.utils.SimpleAlertDialog;
import com.curdflappers.minesweeper.utils.SoundHelper;

import java.util.Locale;

public class GameActivity extends AppCompatActivity
        implements Game.GameListener {

    private RelativeLayout mFieldView;
    private int mFieldWidth, mFieldHeight, mRotation;
    private static Game game;
    private Handler mHandler;
    private int mInterval = 250; // time delay to update timer (too long makes it skip)
    private long mStartTime = 0L;
    private TextView mTimerView, mMinesLeftView;
    private ModeButtonView mModeButton;
    public static SoundHelper mSoundHelper;
    private boolean mGamePlaying;
    private static SpotView[][] spotViews;
    private Runnable mTimerRunnable = new Runnable() {
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

        mFieldView = findViewById(R.id.minefield);
        mHandler = new Handler();
        mTimerView = findViewById(R.id.timer_view);
        mMinesLeftView = findViewById(R.id.mines_left_view);
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

        if(game == null) game = new Game(this);
        // Set up static array of spots
        if(spotViews == null) {
            int rows = Config.getRows(), cols = Config.getCols();
            spotViews = new SpotView[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    spotViews[r][c] = new SpotView(this);
                    connectSpot(spotViews[r][c], r, c);
                }
            }
        }


        mRotation = ((WindowManager) getSystemService(WINDOW_SERVICE)).
                getDefaultDisplay().getRotation();

        ViewTreeObserver viewTreeObserver = mFieldView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mFieldView.getViewTreeObserver().
                                    removeOnGlobalLayoutListener(this);
                            mFieldWidth = mFieldView.getWidth();
                            mFieldHeight = mFieldView.getHeight();
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

    private void updateTimer() {
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
        int sideLength, offset;
        int rows = Config.getRows(), cols = Config.getCols();
        boolean offsetX;

        // Set up visual formatting
        if(mRotation == 0) {
            sideLength = Math.min(mFieldWidth / cols, mFieldHeight / rows);
            if (sideLength < mFieldWidth / cols) { // horizontal offset
                offset = (mFieldWidth - sideLength * cols) / 2;
                offsetX = true;
            } else { // vertical offset
                offset = (mFieldHeight - sideLength * rows) / 2;
                offsetX = false;
            }
        } else {
            sideLength = Math.min(mFieldHeight / cols, mFieldWidth / rows);
            if (sideLength < mFieldWidth / rows) { // horizontal offset
                offset = (mFieldWidth - sideLength * rows) / 2;
                offsetX = true;
            } else { // vertical offset
                offset = (mFieldHeight - sideLength * cols) / 2;
                offsetX = false;
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                sizeAndPosition(spotViews[r][c],
                        sideLength, r, c, offset, offsetX);
                mFieldView.addView(spotViews[r][c]);
            }
        }
    }

    private void sizeAndPosition(SpotView view,
                                 int sideLength,
                                 int row,
                                 int col,
                                 int offset,
                                 boolean offsetX) {
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(sideLength, sideLength);
        view.setLayoutParams(params);

        if(mRotation == 0) {
            view.setX(col * sideLength);
            view.setY(row * sideLength);
        } else if(mRotation == 1) {
            view.setX(row * sideLength);
            view.setY(mFieldHeight - sideLength * (col + 1));
        } else {
            view.setX((Config.getRows() - row - 1) * sideLength);
            view.setY(col * sideLength);
        }

        if(offsetX) {
            view.setX(view.getX() + offset);
        } else {
            view.setY(view.getY() + (mRotation == 1 ? -offset : offset));
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
        mFieldView.removeAllViews();
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
