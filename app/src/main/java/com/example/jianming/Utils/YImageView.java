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
import android.view.animation.AccelerateInterpolator;
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
                onTouchUp();
                break;
        }

        return true;
    }
    AnimatorSet setX, setY, setYE, setXE;



    private void doXAnimationEnd(long duration) {
        if (this.getX() <= 0 && this.getX() >= minX) {
            return;
        }

        float destX;
        if (this.getX() > 0) {
            destX = 0;
        } else {
            destX = minX;
        }
        setXE = new AnimatorSet();
        setXE.play(ObjectAnimator.ofFloat(this, View.X, this.getX(), destX));

        setXE.setDuration(duration);
        setXE.setInterpolator(new AccelerateInterpolator());

        setXE.start();
    }

    private void doYAnimationEnd(long duration) {
        if (this.getY() <= 0 && this.getY() >= minY) {
            return;
        }

        float destY;

        if (this.getY() > 0) {
            destY = 0;
        } else {
            destY = minY;
        }

        setYE = new AnimatorSet();
        setYE.play(ObjectAnimator.ofFloat(this, View.Y, this.getY(), destY));

        setYE.setDuration(duration);
        setYE.setInterpolator(new AccelerateInterpolator());

        setYE.start();
    }

    private class AnimData {

        public float dest;
        public boolean useAccelerateInterpolator;
        public long duration;
    }

    private AnimData calAnimData(float currPos, int minPos, int velocity) {
        float dest = currPos + velocity * ANIM_DURATION / 2000;
        boolean useAccelerateInterpolator = false;
        if (currPos > 0 || currPos < minPos) {
            useAccelerateInterpolator = true;
            if (currPos > 0) {
                dest = 0;
            } else {
                dest = minPos;
            }
        }

        long aTime;
        long duration = ANIM_DURATION;
        if (dest > 0 || dest < minPos) {
            if (dest > 0) {
                aTime = -(int)currPos * 2 * 1000 / velocity;
            } else {
                aTime = (minPos - (int)currPos) * 2 * 1000 / velocity;
            }
            if (aTime < 0 || aTime > ANIM_DURATION) {
                Log.e("AnimatorSet", "aTime error: " + aTime);
            } else {
                duration = aTime + (ANIM_DURATION - aTime) / 2;
                dest = currPos + velocity * duration / 2000;
            }
        }
        AnimData animData = new AnimData();
        animData.dest = dest;
        animData.useAccelerateInterpolator = useAccelerateInterpolator;
        animData.duration = duration;
        return animData;
    }
    AnimData animDataX, animDataY;

    private void onTouchUp() {



        animDataX = calAnimData(this.getX(), minX, velocityX);


        setX = new AnimatorSet();
        setX.play(ObjectAnimator.ofFloat(this, View.X, this.getX(), animDataX.dest));

        setX.setDuration(animDataX.duration);
        if (animDataX.useAccelerateInterpolator) {
            setX.setInterpolator(new AccelerateInterpolator());
        } else {
            setX.setInterpolator(new DecelerateInterpolator());
        }
        setX.addListener(new AnimatorListenerAdapter() {
            boolean isCanceled = false;
            @Override
            public void onAnimationEnd(Animator animation) {
                velocityX = 0;
                if (!isCanceled) {
                    doXAnimationEnd(ANIM_DURATION - animDataX.duration);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isCanceled = true;
            }
        });
        setX.start();

        animDataY = calAnimData(this.getY(), minY, velocityY);
        setY = new AnimatorSet();
        setY.play(ObjectAnimator.ofFloat(this, View.Y, this.getY(), animDataY.dest));
        setY.setDuration(animDataY.duration);
        if (animDataY.useAccelerateInterpolator) {
            setY.setInterpolator(new AccelerateInterpolator());
        } else {
            setY.setInterpolator(new DecelerateInterpolator());
        }
        setY.addListener(new AnimatorListenerAdapter() {
            boolean isCanceled = false;

            @Override
            public void onAnimationEnd(Animator animation) {
                velocityY = 0;
                if (!isCanceled) {
                    doYAnimationEnd(ANIM_DURATION - animDataY.duration);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isCanceled = true;
            }
        });
        setY.start();
    }


    int velocityX = 0, velocityY = 0;
    void onTouchDown(MotionEvent event) {
        if (setX != null && setX.isRunning()) {
            setX.cancel();
        }
        if (setY != null && setY.isRunning()) {
            setY.cancel();
        }
        if (setXE != null && setXE.isRunning()) {
            setXE.cancel();
        }
        if (setYE != null && setYE.isRunning()) {
            setYE.cancel();
        }

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
