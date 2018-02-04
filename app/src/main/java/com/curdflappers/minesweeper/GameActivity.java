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
    private TextView mTimerView, mMinesLeftView;
    private ModeButtonView mModeButton;
    private SpotView[][] spotViews;
    private Handler mHandler;
    private int mFieldWidth, mFieldHeight, mRotation;
    private static Game game;
    private static long mStartTime;
    public static SoundHelper mSoundHelper;
    private static boolean mGamePlaying;
    private static final String TIMER = "timer", MINES = "mines";
    private Runnable mTimerRunnable = new Runnable() {
        private static final int INTERVAL = 250;

        @Override
        public void run() {
            if (mGamePlaying) {
                if (mStartTime == 0L) {
                    mStartTime = System.currentTimeMillis();
                }
                try {
                    updateTimer();
                } finally {
                    mHandler.postDelayed(mTimerRunnable, INTERVAL);
                }
            }
        }
    };

    private void setGamePlaying(boolean playing) {
        mGamePlaying = playing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setToFullScreen();
        HighScoreHelper.clearTopScores(this);

        mFieldView = findViewById(R.id.minefield);
        mTimerView = findViewById(R.id.timer_view);
        mMinesLeftView = findViewById(R.id.mines_left_view);
        mModeButton = findViewById(R.id.mode_button);
        mHandler = new Handler();
        mRotation = ((WindowManager) getSystemService(WINDOW_SERVICE)).
                getDefaultDisplay().getRotation();

        // Set up the game
        if (game == null) {
            game = new Game(this);
        } else {
            game.setListener(this);
        }
        if (mGamePlaying) startTimer();

        // Set up the spot views
        int rows = game.getRows(), cols = game.getCols();
        spotViews = new SpotView[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                spotViews[r][c] = new SpotView(this);
                connectSpot(spotViews[r][c], r, c);
            }
        }

        // Set up status bar
        if(savedInstanceState != null) {
            mTimerView.setText(savedInstanceState.getString(TIMER));
            mMinesLeftView.setText(savedInstanceState.getString(MINES));
            updateModeButton();
        }

        // Set up the sound helper
        if (mSoundHelper == null) {
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


        mModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.toggleMode();
                updateModeButton();
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
                            showField();
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
    }

    private void updateModeButton() {
        if (game.getSweepMode())
            mModeButton.setImageResource(R.drawable.mine_icon);
        else mModeButton.setImageResource(R.drawable.flag_icon);
    }

    private void updateTimer() {
        long millisElapsed = (int) (System.currentTimeMillis() - mStartTime);
        mTimerView.setText(timeFormat((int) millisElapsed / 1000));
    }

    private void connectSpot(SpotView view, int row, int col) {
        Spot spot = game.getSpots()[row][col];
        view.setSpot(spot);
        spot.setView(view);
        view.setOnClickListener(game);
        view.setOnLongClickListener(game);
    }

    private void showField() {
        int sideLength, offset;
        int rows = game.getRows(), cols = game.getCols();
        boolean offsetX;

        // Set up visual formatting
        if (mRotation == 0) {
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

        if (mRotation == 0) {
            view.setX(col * sideLength);
            view.setY(row * sideLength);
        } else if (mRotation == 1) {
            view.setX(row * sideLength);
            view.setY(mFieldHeight - sideLength * (col + 1));
        } else {
            view.setX((Config.getRows() - row - 1) * sideLength);
            view.setY(col * sideLength);
        }

        if (offsetX) {
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
        if (mGamePlaying) mSoundHelper.playMusic();
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
        setGamePlaying(true);
        startTimer();
        mSoundHelper.playMusic();
    }

    @Override
    public void gameOver(boolean win) {
        setGamePlaying(false);
        stopTimer();
        mSoundHelper.pauseMusic();
        int score = (int) ((System.currentTimeMillis() - mStartTime) / 1000);
        if (win) {
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
        setGamePlaying(false);
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

    private static String timeFormat(int seconds) {
        int minutes = seconds / 60;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TIMER, mTimerView.getText().toString());
        outState.putString(MINES, mMinesLeftView.getText().toString());
    }

    public static void reset() {
        game = null;
        mStartTime = 0L;
        mGamePlaying = false;
    }
}
