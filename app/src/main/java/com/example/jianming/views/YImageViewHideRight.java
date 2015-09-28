package com.example.jianming.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Jianming on 2015/9/28.
 */
public class YImageViewHideRight extends ImageView {
    public YImageViewHideRight(Context context) {
        super(context);
    }

    public YImageViewHideRight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YImageViewHideRight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

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
        return super.setFrame(screamW - 48, 0, screamW - 48 + bitmap_W, bitmap_H);
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
}
