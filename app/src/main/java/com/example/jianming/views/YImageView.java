package com.example.jianming.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

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

//    int start_top, start_left, start_right, start_bottom;
    int screamH, screamW;

    public void setHideLeft(YImageViewHideLeft hideLeft) {
        this.hideLeft = hideLeft;
    }

    public void setHideRight(YImageViewHideRight hideRight) {
        this.hideRight = hideRight;
    }

    YImageViewHideLeft hideLeft;

    YImageViewHideRight hideRight;

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        screamH = b - t;
        screamW = r - l;
        minX = screamW - bitmap_W;
        minY = screamH - bitmap_H;
        if (minY > 0) {
            minY = 0;
        }
        return super.setFrame(0, 0, bitmap_W, bitmap_H);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i("onLayout", getTop() + " " + getLeft() + " " + getRight() + " " + getBottom());

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
        setX.playTogether(
                ObjectAnimator.ofFloat(this, View.X, this.getX(), destX),
                ObjectAnimator.ofFloat(hideLeft, View.X, hideLeft.getX(), destX - hideLeft.getBitmap_W() + 48),
                ObjectAnimator.ofFloat(hideRight, View.X, hideRight.getX(), destX + screamW - 48)
        );
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
        setX.playTogether(
                ObjectAnimator.ofFloat(this, View.X, this.getX(), animDataX.dest),
                ObjectAnimator.ofFloat(hideLeft, View.X, hideLeft.getX(), animDataX.dest - hideLeft.getBitmap_W() + 48),
                ObjectAnimator.ofFloat(hideRight, View.X, hideRight.getX(), animDataX.dest + screamW - 48)
                );
        setX.setDuration(animDataX.duration);

        if (animDataX.useAccelerateInterpolator) {
            setX.setInterpolator(new AccelerateInterpolator());
        } else {
            postXEdgeEvent();
            setX.setInterpolator(new DecelerateInterpolator());
        }

        setX.addListener(new AnimatorListenerAdapter() {
            boolean isCanceled = false;
            @Override
            public void onAnimationEnd(Animator animation) {
                velocityX = 0;
                if (!isCanceled) {
                    if (animDataX.duration < ANIM_DURATION) {
                        postXEdgeEvent();
                        doXAnimationEnd(ANIM_DURATION - animDataX.duration);
                    }
                }
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
            postYEdgeEvent();
            setY.setInterpolator(new DecelerateInterpolator());
        }
        setY.addListener(new AnimatorListenerAdapter() {
            boolean isCanceled = false;

            @Override
            public void onAnimationEnd(Animator animation) {
                velocityY = 0;
                if (!isCanceled) {
                    if (animDataY.duration < ANIM_DURATION) {
                        postYEdgeEvent();
                        doYAnimationEnd(ANIM_DURATION - animDataY.duration);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isCanceled = true;
            }
        });
        setY.start();
    }

    interface EdgeListener {
        void onXEdge(YImageView yImageView);
        void onYEdge(YImageView yImageView);
    }

    private EdgeListener edgeListener = null;

    public void setEdgeListener(EdgeListener edgeListener) {
        this.edgeListener = edgeListener;
    }

    private void postXEdgeEvent() {
        if (edgeListener != null) {
            edgeListener.onXEdge(this);
        }
    }

    private void postYEdgeEvent() {
        if (edgeListener != null) {
            edgeListener.onYEdge(this);
        }
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

        float diffX = (newX - current_x);
        float diffY = (newY - current_y);

        float newImgX = currImgX + diffX;
        float newImgY = currImgY + diffY;
        this.setX(newImgX);
        this.setY(newImgY);

        hideLeft.addDiff(diffX);
        hideRight.addDiff(diffX);


        current_x = (int) event.getRawX();
        current_y = (int) event.getRawY();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        bitmap_W = bm.getWidth();
        bitmap_H = bm.getHeight();
    }

    int bitmap_W, bitmap_H;

    public String picSize() {
        return bitmap_W + " * " + bitmap_H;
    }
}
