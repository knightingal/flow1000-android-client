package org.nanking.knightingal.kslideviewlib

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.knightingal.flow1000.client.R


class YImageSlider : ViewGroup {
    companion object {

        val TAG = "YImageSlider"
        val SPLITE_W = 48
    }
    constructor(context:Context) : super(context) {
        init(context)
    }


    constructor(context:Context, attrs:AttributeSet):super(context, attrs) {
        init(context)
    }

    constructor(context:Context, attrs:AttributeSet, defStyleAttr:Int):super(context, attrs, defStyleAttr) {
        init(context);
    }


    private fun init(context:Context) {
        contentView = YImageView(context, this, 0, )
        hideLeft = YImageView(context, this, -1, )
        hideRight = YImageView(context, this, 1, )

        contentView.postGetBackImg = this::onGetBackImg
        hideLeft.postGetBackImg = this::onGetBackImg
        hideRight.postGetBackImg = this::onGetBackImg

        contentView.postGetNextImg = { onGetNextImg() }
        hideLeft.postGetNextImg = { onGetNextImg() }
        hideRight.postGetNextImg = { onGetNextImg() }

        contentView.postXEdgeEvent = this.onXEdge
        hideLeft.postXEdgeEvent = this.onXEdge
        hideRight.postXEdgeEvent = this.onXEdge

        contentView.postYEdgeEvent = { onYEdge() }
        hideLeft.postYEdgeEvent = { onYEdge() }
        hideRight.postYEdgeEvent = { onYEdge() }

        backButton = ImageView(context)
        nextButton = ImageView(context)


        backButton.setImageResource(R.drawable.ic_keyboard_arrow_left_black_48dp)
        nextButton.setImageResource(R.drawable.ic_keyboard_arrow_right_black_48dp)
        backButton.setBackgroundColor(Color.parseColor("#80000000"))
        nextButton.setBackgroundColor(Color.parseColor("#80000000"))

        backButton.visibility = View.INVISIBLE

        nextButton.setOnClickListener { contentView.doNextImgAnim() }

        backButton.setOnClickListener { contentView.doBackImgAnim() }

        addView(contentView)
        addView(hideLeft)
        addView(hideRight)

        addView(backButton)
        addView(nextButton)
    }

    lateinit var contentView:YImageView
    lateinit var hideLeft:YImageView
    lateinit var hideRight:YImageView


    private lateinit var backButton:ImageView

    private lateinit var nextButton:ImageView



    override fun onLayout(changed:Boolean, l:Int, t:Int, r:Int, b:Int) {
        val width = r - l
        val height = b - t
        contentView.layout(0, 0, width, height)
        hideLeft.layout(0, 0, width, height)
        hideRight.layout(0, 0, width, height)

        backButton.layout(0, height / 2 - 24, 48, height / 2 + 24)
        nextButton.layout(width - 48, height / 2 - 24, width, height / 2 + 24)
    }

    val onXEdge : ()->Unit = {
        Log.d(TAG, "onXEdge")
    }

    fun onYEdge() {
        Log.d(TAG, "onYEdge")
    }

    fun setHideLeftSrc(index:Int) {
        if (imgChangeListener != null) {
            val src = imgChangeListener?.getImgSrcByIndex(index - 1, this)
            val yImageView = hideLeft
            if (src != null) {
                val mb = BitmapFactory.decodeFile(src)
                yImageView.setImageBitmap(mb)
                yImageView.isDisplay = true
            } else {
                yImageView.isDisplay = false
            }
        }
    }

    fun setContentSrc(index:Int) {

        if (imgChangeListener != null) {
            val src = imgChangeListener?.getImgSrcByIndex(index, this);
            val yImageView = contentView
            if (src != null) {
                val mb = BitmapFactory.decodeFile(src)
                yImageView.setImageBitmap(mb)
                yImageView.isDisplay = true
            } else {
                yImageView.isDisplay = false
            }
        }
    }

    fun setHideRightSrc(index:Int) {
        if (imgChangeListener != null) {
            val src = imgChangeListener?.getImgSrcByIndex(index + 1, this)
            val yImageView = hideRight
            if (src != null) {
                val mb = BitmapFactory.decodeFile(src)
                yImageView.setImageBitmap(mb)
//                ImageLoader.getInstance().displayImage(src, yImageView, DIOptionsNoneScaled.options)
                yImageView.isDisplay = true
            } else {
                yImageView.isDisplay = false
            }
        }
    }

    public interface ImgChangeListener {
        fun onGetBackImg(yImageSlider:YImageSlider):String

        fun onGetNextImg(yImageSlider:YImageSlider):String

        fun getImgSrcByIndex(index:Int, yImageSlider:YImageSlider):String
    }


    var imgChangeListener:ImgChangeListener? = null

    fun onGetBackImg() {
        Log.d(TAG, "onGetBackImg")
        var tmp = contentView
        contentView = hideLeft
        hideLeft = hideRight
        hideRight = tmp

        contentView.locationIndex = (0)
        hideLeft.locationIndex = (-1)
        hideRight.locationIndex = (1)
        alingLeftOrRight = 0
        var imgUrl:String? = null

        if (imgChangeListener != null) {
            imgUrl = imgChangeListener?.onGetBackImg(this)
        }
        if (imgUrl != null) {
            if (nextButton.visibility == View.INVISIBLE) {
                nextButton.visibility = (View.VISIBLE)
            }
            val mb = BitmapFactory.decodeFile(imgUrl)
            hideLeft.setImageBitmap(mb)

//            ImageLoader.getInstance().displayImage(imgUrl, hideLeft, DIOptionsNoneScaled.options)
            hideLeft.isDisplay=(true)
        } else {
            backButton.visibility = (View.INVISIBLE)
//            ImageLoader.getInstance().displayImage(null, hideLeft)
            hideLeft.isDisplay = (false);
        }
        requestLayout()

    }

    fun onGetNextImg() {
        Log.d(TAG, "onGetNextImg");
        var tmp = contentView
        contentView = hideRight
        hideRight = hideLeft
        hideLeft = tmp

        contentView.locationIndex = (0)
        hideLeft.locationIndex = (-1)
        hideRight.locationIndex = (1)
        alingLeftOrRight = 1
        var imgUrl:String? = null
        if (imgChangeListener != null) {
            imgUrl = imgChangeListener?.onGetNextImg(this);
        }
        if (imgUrl != null) {
            if (backButton.visibility == View.INVISIBLE) {
                backButton.visibility = (View.VISIBLE)
            }
            val mb = BitmapFactory.decodeFile(imgUrl)
            hideRight.setImageBitmap(mb)
            hideRight.isDisplay = (true)
        } else {
            nextButton.visibility = (View.INVISIBLE)
            hideRight.isDisplay = (false)
        }
        requestLayout()
    }


    var alingLeftOrRight:Int = 0;


}
