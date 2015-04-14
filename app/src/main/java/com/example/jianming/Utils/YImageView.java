package com.example.jianming.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
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
    int screamH, screamW;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        screamH = display.getHeight();
        screamW = display.getWidth();

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
        addVelocityTracker(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);

                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;

            case MotionEvent.ACTION_UP:
                int velocityX = getScrollVelocity();
                Log.d("onTouchEvent", "velocityX: " + velocityX);
                recycleVelocityTracker();
                onTouchMove(event);
                break;
        }

        return true;
    }

    private VelocityTracker velocityTracker;
    private int getScrollVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        return  (int) velocityTracker.getXVelocity();

    }

    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    void onTouchDown(MotionEvent event) {
        current_x = (int) event.getRawX();
        current_y = (int) event.getRawY();
    }

    int newX, newY, current_x, current_y;

    void onTouchMove(MotionEvent event) {

        float currImgX = this.getX();
        float currImgY = this.getY();

        newX = (int) event.getRawX();
        newY = (int) event.getRawY();

        int diffX = newX - current_x;
        int diffY = newY - current_y;

        float newImgX = currImgX + diffX;
        float newImgY = currImgY + diffY;
        this.setX(newImgX);
        this.setY(newImgY);

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
