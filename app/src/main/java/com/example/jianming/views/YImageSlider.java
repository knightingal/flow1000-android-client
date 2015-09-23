package com.example.jianming.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Jianming on 2015/9/22.
 */
public class YImageSlider extends ViewGroup {
    public YImageSlider(Context context) {
        super(context);
        init(context);
    }

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
        backButton = new ImageView(context);
        nextButton = new ImageView(context);
        addView(contentView);
    }

    private YImageView contentView;

    private ImageView backButton;

    private ImageView nextButton;

    public YImageView getContentView() {
        return contentView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
        contentView.layout(l, t, r, b);
    }
}
