package com.example.jianming.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by Jianming on 2015/3/20.
 */
public class YImageView extends ImageView {
    private int minX = 0, minY = 0;
    private static final int ANIM_DURATION = 500;
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
        minX = screamW - bitmap_W;
        minY = screamH - bitmap_H;
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

            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;
        }

        return true;
    }

    private void onTouchUp(MotionEvent event) {

        float destX = this.getX() + velocityX / 4;
        float destY = this.getY() + velocityY / 4;

        if (destX > 0) {
            destX = 0;
        } else if (destX < minX) {
            destX = minX;
        }

        if (destY > 0) {
            destY = 0;
        } else if (destY < minY) {
            destY = minY;
        }

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(this, View.X, this.getX(), destX))
            .with(ObjectAnimator.ofFloat(this, View.Y, this.getY(), destY));

        set.setDuration(500);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                velocityX = 0;
                velocityY = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                velocityX = 0;
                velocityY = 0;
            }
        });
        set.start();
    }


    int velocityX = 0, velocityY = 0;
    void onTouchDown(MotionEvent event) {

        current_x = lastX = event.getRawX();
        current_y = lastY = event.getRawY();
        lastEventTime = event.getEventTime();
    }

    float lastX, lastY;

    long lastEventTime;

    float newX, newY, current_x, current_y;

    void onTouchMove(MotionEvent event) {

        newX = event.getRawX();
        newY = event.getRawY();
        long currEventTime = event.getEventTime();
        if (currEventTime - lastEventTime > 30) {
            float dX = newX - lastX;
            float dY = newY - lastY;
            long dTime = currEventTime - lastEventTime;
            velocityX = (int) (dX * 1000 / dTime);
            velocityY = (int) (dY * 1000 / dTime);
            Log.d("AnimatorSet", "velocityX: " + velocityX);
            Log.d("AnimatorSet", "velocityY: " + velocityY);
            lastX = newX;
            lastY = newY;
            lastEventTime = currEventTime;
        }


        float currImgX = this.getX();
        float currImgY = this.getY();



        int diffX = (int) (newX - current_x);
        int diffY = (int) (newY - current_y);

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
