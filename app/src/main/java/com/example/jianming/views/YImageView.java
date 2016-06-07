package com.example.jianming.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;

public class YImageView extends ImageView {
    private YImageSlider yImageSlider;

    public void setLocationIndex(int locationIndex) {
        this.locationIndex = locationIndex;
    }

    private boolean isDisplay = true;

    public void setDisplay() {
        isDisplay = true;
    }

    public void setNoDisplay() {
        isDisplay = false;
    }

    private int locationIndex;
    private int minX = 0, minY = 0;
    private static final int ANIM_DURATION = 500;

    public YImageView(Context context, YImageSlider yImageSlider, int locationIndex) {
        super(context);
        this.yImageSlider = yImageSlider;
        this.locationIndex = locationIndex;
    }

    int screamH, screamW;

    int originY;

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        screamH = b - t;
        screamW = r - l;
        minX = screamW - bitmap_W;
        minY = screamH - bitmap_H;
        if (minY > 0) {
            minY = 0;
        }

        int left, top, right, bottom;
        int contentImageWidth = yImageSlider.getContentView().getBitmap_W();
        if (locationIndex == 1) {
            if (yImageSlider.getAlingLeftOrRight() == 0) {
                left = contentImageWidth + YImageSlider.SPLITE_W;
                top = 0;
                right = contentImageWidth + YImageSlider.SPLITE_W + bitmap_W;
                bottom = bitmap_H;
            } else {
                left = screamW + YImageSlider.SPLITE_W;
                top = 0;
                right = screamW + YImageSlider.SPLITE_W + bitmap_W;
                bottom = bitmap_H;
            }
        } else if (locationIndex == -1) {
            if (yImageSlider.getAlingLeftOrRight() == 0) {
                left = -bitmap_W - YImageSlider.SPLITE_W;
                top = 0;
                right = -YImageSlider.SPLITE_W;
                bottom = bitmap_H;
            } else {
                left = -(bitmap_W + YImageSlider.SPLITE_W + contentImageWidth - screamW);
                top = 0;
                right = -(YImageSlider.SPLITE_W + contentImageWidth - screamW);
                bottom = bitmap_H;
            }
        } else {
            if (yImageSlider.getAlingLeftOrRight() == 0) {
                left = 0;
                top = 0;
                right = bitmap_W;
                bottom = bitmap_H;
            } else {
                left = -(contentImageWidth - screamW);
                top = 0;
                right = screamW;
                bottom = bitmap_H;
            }
        }

        top = (screamH - bitmap_H) / 2;
        originY = top;
        boolean isChanged = super.setFrame(0, 0, bitmap_W, bitmap_H);
        setX(left);
        setY(top);
        return isChanged;
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
        Collection<Animator> animators = new ArrayList<Animator>();

        animators.add(ObjectAnimator.ofFloat(this, View.X, this.getX(), destX));
        if (yImageSlider.getHideLeft().isDisplay) {
            animators.add(ObjectAnimator.ofFloat(yImageSlider.getHideLeft(), View.X, yImageSlider.getHideLeft().getX(), destX - yImageSlider.getHideLeft().getBitmap_W() - YImageSlider.SPLITE_W));
        }
        if (yImageSlider.getHideRight().isDisplay) {
            animators.add(ObjectAnimator.ofFloat(yImageSlider.getHideRight(), View.X, yImageSlider.getHideRight().getX(), destX + getBitmap_W() + YImageSlider.SPLITE_W));
        }
        setXE.playTogether(animators);
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
//        setYE.play(ObjectAnimator.ofFloat(this, View.Y, this.getY(), destY));
        setYE.playTogether(ObjectAnimator.ofFloat(this, View.Y, this.getY(), destY),
                ObjectAnimator.ofFloat(yImageSlider.getHideRight(), View.Y, yImageSlider.getHideRight().getY(), destY),
                ObjectAnimator.ofFloat(yImageSlider.getHideLeft(), View.Y, yImageSlider.getHideLeft().getY(), destY)
        );
        setYE.setDuration(duration);
        setYE.setInterpolator(new AccelerateInterpolator());
        setYE.start();
    }

    private class AnimData {

        public float dest;
        public boolean useAccelerateInterpolator;
        public long duration;
    }

    private AnimData calAnimDataX(float currPos, int minPos, int velocity) {
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
                aTime = -(int) currPos * 2 * 1000 / velocity;
            } else {
                aTime = (minPos - (int) currPos) * 2 * 1000 / velocity;
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

    private AnimData calAnimDataY() {
        return calAnimDataY(this.getY(), this.minY, this.velocityY);
    }

    private AnimData calAnimDataY(float currPos, int minPos, int velocity) {
        float dest = currPos + velocity * ANIM_DURATION / 2000;
        boolean useAccelerateInterpolator = false;
        long duration = ANIM_DURATION;
        if (bitmap_H < screamH) {
            dest = originY;
        } else {

            if (currPos > 0 || currPos < minPos) {
                useAccelerateInterpolator = true;
                if (currPos > 0) {
                    dest = 0;
                } else {
                    dest = minPos;
                }
            }

            long aTime;

            if (dest > 0 || dest < minPos) {
                if (dest > 0) {
                    aTime = -(int) currPos * 2 * 1000 / velocity;
                } else {
                    aTime = (minPos - (int) currPos) * 2 * 1000 / velocity;
                }
                if (aTime < 0 || aTime > ANIM_DURATION) {
                    Log.e("AnimatorSet", "aTime error: " + aTime);
                } else {
                    duration = aTime + (ANIM_DURATION - aTime) / 2;
                    dest = currPos + velocity * duration / 2000;
                }
            }
        }
        AnimData animData = new AnimData();
        animData.dest = dest;
        animData.useAccelerateInterpolator = useAccelerateInterpolator;
        animData.duration = duration;
        return animData;
    }

    AnimData animDataX, animDataY;

    public void doBackImgAnim() {
        setX = new AnimatorSet();
        Collection<Animator> animators = new ArrayList<Animator>();

        animators.add(ObjectAnimator.ofFloat(this, View.X, this.getX(), yImageSlider.getHideLeft().getBitmap_W() + YImageSlider.SPLITE_W));
        if (yImageSlider.getHideLeft().isDisplay) {
            animators.add(ObjectAnimator.ofFloat(yImageSlider.getHideLeft(), View.X, yImageSlider.getHideLeft().getX(), 0));
        }
        setX.playTogether(animators);
        setX.setDuration(ANIM_DURATION);
        setX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                postGetBackImg();
            }
        });
        setX.start();
    }

    public void doNextImgAnim() {
        setX = new AnimatorSet();
        Collection<Animator> animators = new ArrayList<Animator>();

        animators.add(ObjectAnimator.ofFloat(this, View.X, this.getX(), -(this.getBitmap_W() + YImageSlider.SPLITE_W + yImageSlider.getHideRight().getBitmap_W() - screamW)));
        if (yImageSlider.getHideRight().isDisplay) {
            animators.add(ObjectAnimator.ofFloat(yImageSlider.getHideRight(), View.X, yImageSlider.getHideRight().getX(), -(yImageSlider.getHideRight().getBitmap_W() - screamW)));
        }
        setX.playTogether(animators);
        setX.setDuration(ANIM_DURATION);
        setX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                postGetNextImg();
            }
        });
        setX.start();
    }

    private void onTouchUp() {
        float upX = this.getX();
        if ((upX > screamW / 3 || (upX > 0 && isOnLeftEdge)) && yImageSlider.getHideLeft().isDisplay) {
            doBackImgAnim();
        } else if ((upX + this.getBitmap_W() < screamW * 2 / 3 || (upX < screamW - this.getBitmap_W() && isOnRightEdge)) && yImageSlider.getHideRight().isDisplay) {
            doNextImgAnim();
        } else {
            animDataX = calAnimDataX(this.getX(), minX, velocityX);
            setX = new AnimatorSet();
            Collection<Animator> animators = new ArrayList<Animator>();
            animators.add(ObjectAnimator.ofFloat(this, View.X, this.getX(), animDataX.dest));
            if (yImageSlider.getHideLeft().isDisplay) {
                animators.add(ObjectAnimator.ofFloat(yImageSlider.getHideLeft(), View.X, yImageSlider.getHideLeft().getX(), animDataX.dest - yImageSlider.getHideLeft().getBitmap_W() - YImageSlider.SPLITE_W));
            }
            if (yImageSlider.getHideRight().isDisplay) {
                animators.add(ObjectAnimator.ofFloat(yImageSlider.getHideRight(), View.X, yImageSlider.getHideRight().getX(), animDataX.dest + getBitmap_W() + YImageSlider.SPLITE_W));
            }
            setX.playTogether(animators);
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
                    Log.d("", "hideleft.getX()" + yImageSlider.getHideLeft().getX());
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
        }
        animDataY = calAnimDataY(this.getY(), minY, velocityY);
        AnimData animDataYLeft = yImageSlider.getHideLeft().calAnimDataY();
        AnimData animDataYRight = yImageSlider.getHideRight().calAnimDataY();

        setY = new AnimatorSet();
//        setY.play(ObjectAnimator.ofFloat(this, View.Y, this.getY(), animDataY.dest));
        setY.playTogether(ObjectAnimator.ofFloat(this, View.Y, this.getY(), animDataY.dest),
                ObjectAnimator.ofFloat(yImageSlider.getHideRight(), View.Y, yImageSlider.getHideRight().getY(), animDataYRight.dest),
                ObjectAnimator.ofFloat(yImageSlider.getHideLeft(), View.Y, yImageSlider.getHideLeft().getY(), animDataYLeft.dest)
                );
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

    private void postGetBackImg() {
        if (edgeListener != null) {
            edgeListener.onGetBackImg(this);
        }
    }

    private void postGetNextImg() {
        if (edgeListener != null) {
            edgeListener.onGetNextImg(this);
        }
    }

    interface EdgeListener {
        void onXEdge(YImageView yImageView);

        void onYEdge(YImageView yImageView);

        void onGetBackImg(YImageView yImageView);

        void onGetNextImg(YImageView yImageView);
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

    boolean isOnLeftEdge = false;
    boolean isOnRightEdge = false;

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

        isOnLeftEdge = this.getX() >= 0;

        isOnRightEdge = this.getX() <= screamW - getBitmap_W();

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

        yImageSlider.getHideLeft().addDiff(diffX, diffY);
        yImageSlider.getHideRight().addDiff(diffX, diffY);


        current_x = (int) event.getRawX();
        current_y = (int) event.getRawY();
    }

    public void addDiff(float diffX, float diffY) {
        setX(getX() + diffX);
        setY(getY() + diffY);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        bitmap_W = bm.getWidth();
        bitmap_H = bm.getHeight();
    }

    public int getBitmap_W() {
        return bitmap_W;
    }

    private int bitmap_W, bitmap_H;

}
