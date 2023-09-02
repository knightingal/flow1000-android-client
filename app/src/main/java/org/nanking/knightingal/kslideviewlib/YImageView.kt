package org.nanking.knightingal.kslideviewlib

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView

import java.util.ArrayList

@SuppressLint("ViewConstructor")
class YImageView(
    context: Context,
    private val yImageSlider: YImageSlider,
    var locationIndex: Int,
    id: Int
) : AppCompatImageView(context) {
    companion object {
        private const val TAG = "YImageView"
        private const val ANIM_DURATION:Long = 500
    }

    var isDisplay = true
    var vx = 0.toFloat()
    var vy = 0.toFloat()

    private var lastX = 0.toFloat()
    private var lastY = 0.toFloat()
    private var lastEventTime = 0.toLong()
    private var currentX = 0.toFloat()
    private var currentY = 0.toFloat()
    private var newX = 0.toFloat()
    private var newY = 0.toFloat()
    private var bitmapW:Int = 0
    private var bitmapH:Int = 0


    private var minX = 0
    private var minY = 0

    private var screamH:Int = 0
    private var screamW:Int = 0

    private var originY:Int = 0

    private val fitAllImageSize: Boolean = true

    private var setX: AnimatorSet = AnimatorSet()
    private var setY: AnimatorSet = AnimatorSet()
    private var setYE: AnimatorSet = AnimatorSet()
    private var setXE: AnimatorSet = AnimatorSet()
    lateinit var animDataX:AnimData
    lateinit var animDataY:AnimData

    lateinit var postGetBackImg: ()->Unit
    lateinit var postGetNextImg: ()->Unit

    lateinit var postXEdgeEvent: ()->Unit
    lateinit var postYEdgeEvent: ()->Unit

    private var isOnLeftEdge = false
    private var isOnRightEdge = false

    override fun setFrame(l:Int, t:Int, r:Int, b:Int):Boolean {
        screamH = b - t
        screamW = r - l
        minX = screamW - bitmapW
        minY = if (screamH - bitmapH > 0) 0 else screamH - bitmapH

        if (bitmapW < screamW || fitAllImageSize) {
            val rat = (bitmapH.toFloat())/(bitmapW.toFloat())
            bitmapW = screamW
            bitmapH = (rat * bitmapW).toInt()
        }

        val contentImageWidth = yImageSlider.contentView.bitmapW

        val left = calLeftPoint(contentImageWidth)

        val right = calRightPoint(contentImageWidth)

        val top = (screamH - bitmapH) / 2
        originY = top
        val bottom = top + bitmapH
        Log.d(TAG, "left=$left, right=$right, top=$top, bottom=$bottom")

        val isChanged = super.setFrame(0, top, bitmapW, bottom)
        x = left.toFloat()
        y = top.toFloat()
        return isChanged;
    }

    private fun calLeftPoint(contentImageWidth: Int): Int {
        when (locationIndex) {
            1 -> {
                return if (yImageSlider.alingLeftOrRight == 0) {
                    contentImageWidth + YImageSlider.SPLITE_W
                } else {
                    screamW + YImageSlider.SPLITE_W
                }
            }
            -1 -> {
                return if (yImageSlider.alingLeftOrRight == 0) {
                    -bitmapW - YImageSlider.SPLITE_W
                } else {
                    -(bitmapW + YImageSlider.SPLITE_W + contentImageWidth - screamW)
                }
            }
            else -> {
                return if (yImageSlider.alingLeftOrRight == 0) {
                    0
                } else {
                    -(contentImageWidth - screamW)
                }
            }
        }
    }

    private fun calRightPoint(contentImageWidth: Int): Int {
        when (locationIndex) {
            1 -> {
                return if (yImageSlider.alingLeftOrRight == 0) {
                    contentImageWidth + YImageSlider.SPLITE_W + bitmapW
                } else {
                    screamW + YImageSlider.SPLITE_W + bitmapW
                }
            }
            -1 -> {
                return if (yImageSlider.alingLeftOrRight == 0) {
                    -YImageSlider.SPLITE_W
                } else {
                    -(YImageSlider.SPLITE_W + contentImageWidth - screamW)
                }
            }
            else -> {
                return if (yImageSlider.alingLeftOrRight == 0) {
                    bitmapW
                } else {
                    screamW
                }
            }
        }
    }

    override fun onTouchEvent(event:MotionEvent):Boolean {
        when (event.action.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> onTouchDown(event)
            MotionEvent.ACTION_MOVE -> onTouchMove(event)
            MotionEvent.ACTION_UP -> onTouchUp()
        }
        return true
    }



    private fun  doXAnimationEnd(duration: Long) {
        if (x.toInt() in minX..0) {
            return
        }

        val destX:Float = if (x > 0) 0.toFloat() else minX.toFloat()

        setXE = AnimatorSet()

        val animators = ArrayList<Animator>()
        animators.add(ObjectAnimator.ofFloat(this, View.X, x, destX))
        animators.add(ObjectAnimator.ofFloat(
                yImageSlider.hideLeft,
                View.X,
                yImageSlider.hideLeft.x,
                destX - yImageSlider.hideLeft.bitmapW - YImageSlider.SPLITE_W
        ))
        animators.add(ObjectAnimator.ofFloat(
                yImageSlider.hideRight,
                View.X,
                yImageSlider.hideRight.x,
                destX + bitmapW + YImageSlider.SPLITE_W
        ))

        setXE.playTogether(animators)
        setXE.duration = duration
        setXE.interpolator = AccelerateInterpolator()
        setXE.start()
    }

    private fun  doYAnimationEnd(duration: Long) {
        if (y.toInt() in minY..0) {
            return
        }
        val destY = if (y > 0) 0 else minY

        setYE = AnimatorSet()
        setYE.playTogether(ObjectAnimator.ofFloat(this, View.Y, y, destY.toFloat()))
        setYE.duration = duration
        setYE.interpolator = AccelerateInterpolator()
        setYE.start()

    }

    private fun hideImageAnimBack() {
        val setYE = AnimatorSet()
        setYE.playTogether(
                ObjectAnimator.ofFloat(
                        yImageSlider.hideRight,
                        View.Y,
                        yImageSlider.hideRight.y,
                        yImageSlider.hideRight.originY.toFloat()
                ),
                ObjectAnimator.ofFloat(
                        yImageSlider.hideLeft,
                        View.Y,
                        yImageSlider.hideLeft.y,
                        yImageSlider.hideLeft.originY.toFloat()
                )
        )
        setYE.duration = ANIM_DURATION
        setYE.interpolator = AccelerateInterpolator()
        setYE.start()
    }

    data class AnimData(var dest:Float, var useAccelerateInterpolator:Boolean, var duration:Long)

    private fun  calAnimDataX(currPos: Float, minPos: Int, velocity: Float): YImageView.AnimData {
        var dest = currPos + velocity * ANIM_DURATION / 2000
        var useAccelerateInterpolator = false
        if (currPos > 0 || currPos < minPos) {
            useAccelerateInterpolator = true
            dest = if (currPos > 0) {
                0.toFloat()
            } else {
                minPos.toFloat()
            }
        }


        var duration = ANIM_DURATION
        if (dest > 0 || dest < minPos) {
            val aTime = if (dest > 0)
                (-currPos * 2 * 1000 / velocity).toLong()
            else
                ((minPos.toFloat() - currPos) * 2 * 1000 / velocity).toLong()

            if (aTime < 0 || aTime > ANIM_DURATION) {
                Log.e(TAG, "aTime error: $aTime")
            } else {
                duration = aTime + (ANIM_DURATION - aTime) / 2
                dest = currPos + velocity * duration / 2000
            }
        }
        return AnimData(dest, useAccelerateInterpolator, duration)
    }


    private fun  calAnimDataY(currPos: Float, minPos: Int, velocity: Float): YImageView.AnimData {
        var dest = currPos + velocity * ANIM_DURATION / 2000
        var useAccelerateInterpolator = false
        var duration = ANIM_DURATION
        if (bitmapH < screamH) {
            dest = originY.toFloat()
        } else {

            if (currPos > 0 || currPos < minPos) {
                useAccelerateInterpolator = true
                dest = if (currPos > 0) {
                    0.toFloat()
                } else {
                    minPos.toFloat()
                }
            }

            if (dest > 0 || dest < minPos) {
                val aTime = if (dest > 0)
                    (-currPos * 2 * 1000 / velocity).toLong()
                else
                    ((minPos.toFloat() - currPos) * 2 * 1000 / velocity).toLong()
                if (aTime < 0 || aTime > ANIM_DURATION) {
                    Log.e(TAG, "aTime error: $aTime")
                } else {
                    duration = aTime + (ANIM_DURATION - aTime) / 2
                    dest = currPos + velocity * duration / 2000
                }
            }
        }
        return AnimData(dest, useAccelerateInterpolator, duration)
    }


    fun doBackImgAnim() {
        setX = AnimatorSet()
        val animators = ArrayList<Animator>()

        animators.add(ObjectAnimator.ofFloat(
                this,
                View.X,
                x,
                (yImageSlider.hideLeft.bitmapW + YImageSlider.SPLITE_W).toFloat()
        ))
        animators.add(ObjectAnimator.ofFloat(
                this,
                View.Y,
                y,
                originY.toFloat()
        ))
        Log.d(TAG, "doBackImgAnim, originY=$originY")
        animators.add(ObjectAnimator.ofFloat(
                yImageSlider.hideLeft, View.X, yImageSlider.hideLeft.x, 0.toFloat()
        ))
        animators.add(ObjectAnimator.ofFloat(
                yImageSlider.hideLeft, View.Y, yImageSlider.hideLeft.y, yImageSlider.hideLeft.originY.toFloat()
        ))
        setX.playTogether(animators)
        setX.duration = ANIM_DURATION
        setX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                postGetBackImg()
            }
        })
        setX.start()
    }

    fun doNextImgAnim() {
        setX = AnimatorSet()
        val animators = ArrayList<Animator>()

        animators.add(ObjectAnimator.ofFloat(
                this,
                View.X,
                x,
                -(bitmapW
                        + YImageSlider.SPLITE_W
                        + yImageSlider.hideRight.bitmapW
                        - screamW).toFloat()
        ))
        Log.d(TAG, "doNextImgAnim, originY=$originY")
        animators.add(ObjectAnimator.ofFloat(
                this,
                View.Y,
                y,
                originY.toFloat()
        ))
        animators.add(ObjectAnimator.ofFloat(
                yImageSlider.hideRight,
                View.X,
                yImageSlider.hideRight.x,
                -(yImageSlider.hideRight.bitmapW - screamW).toFloat()
        ))
        animators.add(ObjectAnimator.ofFloat(
                yImageSlider.hideRight,
                View.Y,
                yImageSlider.hideRight.y,
                yImageSlider.hideRight.originY.toFloat()
        ))
        setX.playTogether(animators)
        setX.duration = ANIM_DURATION
        setX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                postGetNextImg()
            }
        })
        setX.start()
    }

    private fun onTouchUp() {
        val upx = x
        if (upx > 0 && yImageSlider.hideLeft.isDisplay) {
            doBackImgAnim()
        } else if (upx < screamW - this.bitmapW && yImageSlider.hideRight.isDisplay) {
            doNextImgAnim()
        } else if (upx > screamW / 3 && yImageSlider.hideLeft.isDisplay) {
            doBackImgAnim()
        } else if (upx + this.bitmapW < screamW * 2 / 3 && yImageSlider.hideRight.isDisplay) {
            doNextImgAnim()
        } else {
            animDataX = calAnimDataX(x, minX, vx)
            setX = AnimatorSet()
            val animators = ArrayList<Animator>()
            animators.add(ObjectAnimator.ofFloat(this, View.X, x, animDataX.dest))
            animators.add(ObjectAnimator.ofFloat(
                    yImageSlider.hideLeft,
                    View.X,
                    yImageSlider.hideLeft.x,
                    animDataX.dest - yImageSlider.hideLeft.bitmapW - YImageSlider.SPLITE_W
            ))
            animators.add(ObjectAnimator.ofFloat(
                    yImageSlider.hideRight,
                    View.X,
                    yImageSlider.hideRight.x,
                    animDataX.dest + bitmapW + YImageSlider.SPLITE_W
            ))
            setX.playTogether(animators)
            setX.duration = animDataX.duration
            if (animDataX.useAccelerateInterpolator) {
                setX.interpolator = AccelerateInterpolator()
            } else {
                postXEdgeEvent()
                setX.interpolator = DecelerateInterpolator()
            }

            setX.addListener(object : AnimatorListenerAdapter() {
                var isCanceled = false

                override fun onAnimationEnd(animation: Animator) {
                    vx = 0.toFloat()
                    if (!isCanceled) {
                        if (animDataX.duration < ANIM_DURATION) {
                            postXEdgeEvent()
                            doXAnimationEnd(ANIM_DURATION - animDataX.duration)
                        }
                    }

                }

                override fun onAnimationCancel(animation: Animator) {
                    isCanceled = true
                }
            })
            setX.start()
        }
        animDataY = calAnimDataY(y, minY, vy)

        setY = AnimatorSet()
        setY.playTogether(ObjectAnimator.ofFloat(
                this,
                View.Y,
                y,
                animDataY.dest
        ))
        setY.duration = animDataY.duration
        setY.interpolator = if (animDataY.useAccelerateInterpolator)
            AccelerateInterpolator()
        else
            DecelerateInterpolator()
        if (!animDataY.useAccelerateInterpolator) {
            postYEdgeEvent()
        }
        setY.addListener(object :AnimatorListenerAdapter() {
            var isCanceled = false

            override fun onAnimationEnd(animation: Animator) {
                vy = 0.toFloat()
                if (!isCanceled) {
                    if (animDataY.duration < ANIM_DURATION) {
                        postYEdgeEvent()
                        doYAnimationEnd(ANIM_DURATION - animDataY.duration)
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                isCanceled = true
            }
        })
        setY.start()
        hideImageAnimBack()
    }



    private fun  onTouchDown(event: MotionEvent) {
        if (setX.isRunning ) {
            setX.cancel()
        }
        if (setY.isRunning) {
            setY.cancel()
        }
        if (setXE.isRunning) {
            setXE.cancel()
        }
        if (setYE.isRunning) {
            setYE.cancel()
        }

        isOnLeftEdge = this.x >= 0

        isOnRightEdge = this.y <= screamW - bitmapW

        currentX = event.rawX
        lastX = event.rawX

        currentY = event.rawY
        lastY = event.rawY
        lastEventTime = event.eventTime
    }


    private fun onTouchMove(event: MotionEvent) {
        newX = event.rawX
        newY = event.rawY
        val currentEventTime = event.eventTime
        if (currentEventTime - lastEventTime > 30) {
            val dx = newX - lastX
            val dy = newY - lastY
            val dTime = currentEventTime - lastEventTime
            vx = dx * 1000 / dTime
            vy = dy * 1000 / dTime
            lastX = newX
            lastY = newY
            lastEventTime = currentEventTime
        }

        val currImgX = this.x
        val currImgY = this.y

        val diffX = newX - currentX
        val diffY = newY - currentY
        val newImgX = currImgX + diffX
        val newImgY = currImgY + diffY

        this.x = newImgX
        this.y = newImgY

        yImageSlider.hideLeft.x += diffX
        yImageSlider.hideRight.x += diffX

        currentX = event.rawX
        currentY = event.rawY
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        bitmapW = bm!!.width
        bitmapH = bm.height
    }



}
