package com.example.jianming.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Jianming on 2015/3/20.
 */
public class YImageView extends ImageView {
    public YImageView(Context context) {
        super(context);
    }

    public YImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    int start_top, start_left, start_right, start_bottom;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        start_top = top;
        start_left = left;
        start_right = right;
        start_bottom = bottom;
        setFrame(0, 0, bitmap_W, bitmap_H);
        Log.i("onLayout", getTop() + " " + getLeft() + " " + getRight() + " " + getBottom());
        //Log.i("onLayout", top + " " + left + " " + right + " " + bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);

                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
        }

        return true;
    }

    int clicked = 0;

    void onTouchDown(MotionEvent event) {
        //mode = MODE.DRAG;

        current_x = (int) event.getRawX();
        current_y = (int) event.getRawY();

        start_x = (int) event.getX();
        start_y = current_y - this.getTop();
    }

    int current_x, current_y, start_x, start_y;

    void onTouchMove(MotionEvent event) {
        int left = 0, top = 0, right = 0, bottom = 0;
        left = current_x - start_x;
        right = current_x + this.getWidth() - start_x;

        top = current_y - start_y;
        bottom = current_y - start_y + this.getHeight();

        this.setFrame(left, top, right, bottom);

        current_x = (int) event.getRawX();
        current_y = (int) event.getRawY();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        bitmap_W = bm.getWidth();
        bitmap_H = bm.getHeight();
        Log.i ("setImageBitmap", bitmap_H + " " + bitmap_W);
    }
    int bitmap_W, bitmap_H;

    public String picSize() {
        return bitmap_W + " * " + bitmap_H;
    }
}
