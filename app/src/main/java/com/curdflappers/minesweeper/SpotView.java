package com.curdflappers.minesweeper;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.widget.RelativeLayout;

public class SpotView extends AppCompatImageView {
    /**
     * @param context
     * @param sideLength in dp
     */
    public SpotView(Context context, int sideLength, float x, float y) {
        super(context);
        setImageResource(R.drawable.unrevealed);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(sideLength, sideLength);
        setLayoutParams(params);
        setX(x);
        setY(y);
    }
}
