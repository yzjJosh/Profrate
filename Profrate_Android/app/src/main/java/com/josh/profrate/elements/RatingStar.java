package com.josh.profrate.elements;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.josh.profrate.R;

public class RatingStar extends LinearLayout {

    private final static String TAG = "RatingStar";
    private double rate = 0;
    private final int maxRating;


    public RatingStar(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RatingStar, 0, 0);
        int maxRating = a.getInt(R.styleable.RatingStar_maxRating, 5);
        this.maxRating = maxRating > 0? maxRating: 1;
        double rate = a.getFloat(R.styleable.RatingStar_rating, 0);
        this.rate = (rate >=0 && rate <= maxRating)? rate: 0;
        boolean ratable = a.getBoolean(R.styleable.RatingStar_ratable, false);
        removeAllViews();
        for(int i=0; i<maxRating; i++){
            ImageView star = new ImageView(context);
            star.setImageResource(R.drawable.star_empty);
            LayoutParams params = new LayoutParams(25, 25);
            params.setMargins(3, 3, 3, 3);
            addView(star);
            if(ratable){
                star.setClickable(true);
                star.setOnClickListener(new OnStarClickdListener(i));
            }
        }
        setRating(rate);
    }

    public double getRating(){
        return rate;
    }

    public void setRating(double rating){
        rating = 0.5*((int)(rating/0.5));
        rating = rating > maxRating? maxRating: rating;
        this.rate = rating;
        for(int i=0; i<maxRating; i++)
            ((ImageView)getChildAt(i)).setImageResource(R.drawable.star_empty);
        for(int i=0; i<(int)rating; i++)
            ((ImageView)getChildAt(i)).setImageResource(R.drawable.star_full);
        if(rating - (int)rating > 0)
            ((ImageView)getChildAt((int)rating)).setImageResource(R.drawable.star_half);
    }

    private class OnStarClickdListener implements OnClickListener{

        private int starIndex;

        public OnStarClickdListener(int starIndex){
            this.starIndex = starIndex;
        }

        @Override
        public void onClick(View v) {
            double full = (double) (starIndex+1);
            double half = full - 0.5;
            double empty = full - 1.0;
            int curIndex = (int)Math.ceil(rate) -1;
            if(starIndex != curIndex)
                setRating(full);
            else if(rate == full)
                setRating(half);
            else if(rate == half)
                setRating(empty);
        }
    };

}
