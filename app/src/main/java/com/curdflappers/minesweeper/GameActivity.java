package com.curdflappers.minesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

public class GameActivity extends AppCompatActivity {

    RelativeLayout minefield;
    private int rows = 20;
    private int cols = 10;
    private int minefieldWidth, minefieldHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setToFullScreen();

        minefield = findViewById(R.id.minefield);

        ViewTreeObserver viewTreeObserver = minefield.getViewTreeObserver();
        if(viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    minefield.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    minefieldWidth = minefield.getWidth();
                    minefieldHeight = minefield.getHeight();
                    fillMineField(rows, cols);
                }
            });
        }

    }

    private void fillMineField(int rows, int cols) {
        int x = 0, y = 0, offsetX = 0, offsetY = 0;

        int sideLength = Math.min(minefieldWidth / cols, minefieldHeight / rows);
        if(sideLength < minefieldWidth / cols) { // vertical empty space
            // horizontal offset
            offsetX = (minefieldWidth - sideLength * cols) / 2;
        } else {
            offsetY = (minefieldHeight - sideLength * rows) / 2;
        }

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                SpotView spot = new SpotView(this, sideLength, x + offsetX, y + offsetY);
                minefield.addView(spot);
                x += sideLength;
            }
            x = 0;
            y += sideLength;
        }
    }

    private void setToFullScreen()
    {
        ViewGroup rootLayout = findViewById(R.id.activity_game);
        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
