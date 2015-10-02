package com.example.jianming.views;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.example.jianming.Utils.DIOptionsNoneScaled;
import com.example.jianming.myapplication.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * Created by Jianming on 2015/9/22.
 */
public class YImageSlider extends ViewGroup implements YImageView.EdgeListener {
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
        contentView.setEdgeListener(this);
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

        line31 = new View(context);
        line32 = new View(context);

        line31.setBackgroundColor(Color.parseColor("green"));
        line32.setBackgroundColor(Color.parseColor("blue"));

        addView(line31);
        addView(line32);
        ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index] + ""), hideLeft, DIOptionsNoneScaled.getInstance().getOptions());
        ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index + 1] + ""), contentView, DIOptionsNoneScaled.getInstance().getOptions());
        ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index + 2] + ""), hideRight, DIOptionsNoneScaled.getInstance().getOptions());
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

    private View line31, line32;

    public YImageView getContentView() {
        return contentView;
    }

    int index = 0;
    int pics[] = {R.drawable.su27_1, R.drawable.su27_2, R.drawable.su27_3, R.drawable.su27_1, R.drawable.su27_2, R.drawable.su27_3};

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

        line31.layout(width / 3, 0, width / 3 + 1, height);
        line32.layout(width * 2 / 3, 0, width * 2 / 3 + 1, height);
    }

    @Override
    public void onXEdge(YImageView yImageView) {

    }

    @Override
    public void onYEdge(YImageView yImageView) {

    }

    @Override
    public void onGetBackImg(YImageView yImageView) {
        index--;
        if (index < 0) {
            index = 2;
        }
        ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index] + ""), hideLeft, DIOptionsNoneScaled.getInstance().getOptions());
        ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index + 1] + ""), contentView, DIOptionsNoneScaled.getInstance().getOptions());
        ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index + 2] + ""), hideRight, DIOptionsNoneScaled.getInstance().getOptions());
//        hideLeft.setImageBitmap(BitmapFactory.decodeResource(getResources(), pics[index]));
//        contentView.setImageBitmap(BitmapFactory.decodeResource(getResources(), pics[index + 1]));
//        hideRight.setImageBitmap(BitmapFactory.decodeResource(getResources(), pics[index + 2]));
    }
}
