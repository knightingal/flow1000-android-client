package com.example.jianming.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.example.jianming.myapplication.R;

/**
 * Created by Jianming on 2015/9/22.
 */
public class YImageSlider extends ViewGroup {
    public YImageSlider(Context context) {
        super(context);
        init(context);
    }

    public final static int SPLITE_W = 48;

    public YImageSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public YImageSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        contentView = new YImageView(context);
        hideLeft = new YImageViewHideLeft(context);
        hideRight = new YImageViewHideRight(context, contentView);

        contentView.setHideLeft(hideLeft);
        contentView.setHideRight(hideRight);


        backButton = new ImageView(context);
        nextButton = new ImageView(context);


        backButton.setImageResource(R.drawable.ic_keyboard_arrow_left_black_48dp);
        nextButton.setImageResource(R.drawable.ic_keyboard_arrow_right_black_48dp);
        backButton.setBackgroundColor(Color.parseColor("#80000000"));
        nextButton.setBackgroundColor(Color.parseColor("#80000000"));

        addView(contentView);
        addView(hideLeft);
        addView(hideRight);

        addView(backButton);
        addView(nextButton);
    }

    private YImageView contentView;

    public YImageViewHideLeft getHideLeft() {
        return hideLeft;
    }

    public YImageViewHideRight getHideRight() {
        return hideRight;
    }

    private YImageViewHideLeft hideLeft;

    private YImageViewHideRight hideRight;

    private ImageView backButton;

    private ImageView nextButton;

    public YImageView getContentView() {
        return contentView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
        int width = r - l;
        int height = b - t;
        contentView.layout(0, 0, width, height);
        hideLeft.layout(0, 0, width, height);
        hideRight.layout(0, 0, width, height);

        backButton.layout(0, 0, 48, 48);
        nextButton.layout(width - 48, 0, width, 48);
    }
}
