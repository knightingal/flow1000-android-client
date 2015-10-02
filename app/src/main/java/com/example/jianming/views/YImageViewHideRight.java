package com.example.jianming.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Jianming on 2015/9/28.
 */
public class YImageViewHideRight extends ImageView {
    public YImageViewHideRight(Context context, YImageView contentView) {
        super(context);
        this.contentView = contentView;
    }

    public YImageViewHideRight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YImageViewHideRight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    YImageView contentView;

    int screamH, screamW;

    int bitmap_W, bitmap_H;
    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        screamH = b - t;
        screamW = r - l;
//        minX = screamW - bitmap_W;
//        minY = screamH - bitmap_H;
//        if (minY > 0) {
//            minY = 0;
//        }
        setX(contentView.getBitmap_W() + YImageSlider.SPLITE_W);
        setY(0);
        return super.setFrame(contentView.getBitmap_W() + YImageSlider.SPLITE_W, 0, contentView.getBitmap_W() + YImageSlider.SPLITE_W + bitmap_W, bitmap_H);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (bm != null) {
            bitmap_W = bm.getWidth();
            bitmap_H = bm.getHeight();
        } else {
            bitmap_W = bitmap_H = 0;
        }

    }

    public void addDiff(float diffX) {
        setX(getX() + diffX);
    }
}
