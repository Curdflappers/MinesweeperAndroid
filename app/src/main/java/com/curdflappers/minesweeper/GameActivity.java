package com.curdflappers.minesweeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class GameActivity extends AppCompatActivity
        implements Game.GameListener {

    RelativeLayout minefield;
    private int minefieldWidth, minefieldHeight;
    Game game;
    Handler mHandler;
    int mInterval = 250; // time delay to update timer (too long makes it skip)
    long mStartTime = 0L;
    private TextView mTimerView;
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
    private TextView mMinesLeftView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setToFullScreen();

        minefield = findViewById(R.id.minefield);
        mHandler = new Handler();
        mTimerView = findViewById(R.id.timer_view);
        mMinesLeftView = findViewById(R.id.mines_left_view);
        game = new Game(this);

        findViewById(R.id.reset_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        game.reset();
                    }
                });


        final ImageView MODE_BUTTON = findViewById(R.id.mode_button);
        MODE_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.toggleMode();
                if (game.getSweepMode())
                    MODE_BUTTON.setImageResource(R.drawable.mine_icon);
                else MODE_BUTTON.setImageResource(R.drawable.flag_icon);
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
    }

    void updateTimer() {
        int millisElapsed = (int) (System.currentTimeMillis() - mStartTime);
        int seconds = millisElapsed / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        mTimerView.setText(String.format(Locale.getDefault(),
                "%02d:%02d", minutes, seconds));
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
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mTimerRunnable);
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
        mTimerRunnable.run();
    }

    public void stopTimer() {
        mHandler.removeCallbacks(mTimerRunnable);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void gameReset() {
        stopTimer();
        mStartTime = 0L;
        mTimerView.setText("00:00");
    }

    @Override
    public void minesLeftChanged(int minesLeft) {
        mMinesLeftView.setText(String.valueOf(minesLeft));
    }

    @Override
    public void gameOver(boolean win) {
        String msg = win ? "You win!" : "Game over!";
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
        stopTimer();
    }
}
