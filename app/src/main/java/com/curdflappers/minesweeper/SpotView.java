package com.curdflappers.minesweeper;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.widget.RelativeLayout;

import static com.curdflappers.minesweeper.Spot.EXPLODED;
import static com.curdflappers.minesweeper.Spot.FLAGGED;
import static com.curdflappers.minesweeper.Spot.MINE;
import static com.curdflappers.minesweeper.Spot.REVEALED;

public class SpotView extends AppCompatImageView {

    Spot spot;
    public SpotView(Context context) {
        super(context);
        setImageResource(R.drawable.unrevealed);
    }

    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
        update();
    }

    public void update() {
        if(!spot.get(REVEALED) && !spot.get(FLAGGED))
        {
            setImageResource(R.drawable.unrevealed);
            return;
        }

        if(spot.get(FLAGGED)) {
            if(spot.get(REVEALED) && !spot.get(MINE))
                setImageResource(R.drawable.false_flag);
            else
                setImageResource(R.drawable.flag);
            return;
        }

        // at this point the spot is definitely revealed and unflagged
        if(spot.get(MINE))
            if(spot.get(EXPLODED))
                setImageResource(R.drawable.exploded_mine);
            else
                setImageResource(R.drawable.mine);
        else {
            switch (spot.getNeighboringMines()) {
                case (0):
                    setImageResource(R.drawable.num_0);
                    break;
                case (1):
                    setImageResource(R.drawable.num_1);
                    break;
                case (2):
                    setImageResource(R.drawable.num_2);
                    break;
                case (3):
                    setImageResource(R.drawable.num_3);
                    break;
                case (4):
                    setImageResource(R.drawable.num_4);
                    break;
                case (5):
                    setImageResource(R.drawable.num_5);
                    break;
                case (6):
                    setImageResource(R.drawable.num_6);
                    break;
                case (7):
                    setImageResource(R.drawable.num_7);
                    break;
                case (8):
                    setImageResource(R.drawable.num_8);
                    break;
            }
        }
    }
}
