package com.curdflappers.minesweeper;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.widget.RelativeLayout;

public class SpotView extends AppCompatImageView {
    public Spot spot;

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

    public void update() {
        if(spot.getExploded())
        {
            setImageResource(R.drawable.exploded_mine);
            return;
        }
        if(spot.getRevealed()) {
            if(spot.getFlagged() && !spot.getMine()) {
                setImageResource(R.drawable.false_flag);
            }
            switch(spot.getNeighboringMines()) {
                case(0):
                    setImageResource(R.drawable.num_0);
                    break;
                case(1):
                    setImageResource(R.drawable.num_1);
                    break;
                case(2):
                    setImageResource(R.drawable.num_2);
                    break;
                case(3):
                    setImageResource(R.drawable.num_3);
                    break;
                case(4):
                    setImageResource(R.drawable.num_4);
                    break;
                case(5):
                    setImageResource(R.drawable.num_5);
                    break;
                case(6):
                    setImageResource(R.drawable.num_6);
                    break;
                case(7):
                    setImageResource(R.drawable.num_7);
                    break;
                case(8):
                    setImageResource(R.drawable.num_8);
                    break;
            }

        }
    }
}
