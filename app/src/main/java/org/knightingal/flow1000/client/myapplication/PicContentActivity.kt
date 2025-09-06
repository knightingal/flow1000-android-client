package org.knightingal.flow1000.client.myapplication

import android.app.Activity
import android.os.Bundle
import org.nanking.knightingal.kslideviewlib.YImageSlider
import org.nanking.knightingal.kslideviewlib.YImageSlider.ImgChangeListener
import org.knightingal.flow1000.client.R

class PicContentActivity : Activity(), ImgChangeListener {
    var mImageSlider: YImageSlider? = null

    private var imgArray: Array<String>? = null

    private var position = 0


    override fun onResume() {
        super.onResume()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pic_content)
        val imageSlider = findViewById<YImageSlider>(R.id.image)

        imageSlider.imgChangeListener = this
        imgArray = intent.getStringArrayExtra("imgArray")
        position = intent.getIntExtra("position", 0)
        if (imgArray != null && imgArray!!.size != 0) {
            index = position
        }
        imageSlider.setHideLeftSrc(index)
        imageSlider.setContentSrc(index)
        imageSlider.setHideRightSrc(index)
        mImageSlider = imageSlider
    }

    private fun getImgByIndex(index: Int): String {
        return imgArray!![index % imgArray!!.size]
    }

    var index: Int = 0


//    override fun onBackPressed() {
//        val intent = Intent()
//        intent.putExtra("position", index)
//        setResult(RESULT_OK, intent)
//        super.onBackPressed()
//    }

    override fun onGetBackImg(yImageSlider: YImageSlider): String {
        index = (index + imgArray!!.size) % imgArray!!.size
        index--
        return getImgSrcByIndex(index - 1)
    }

    override fun onGetNextImg(yImageSlider: YImageSlider): String {
        index = index % imgArray!!.size
        index++
        return getImgSrcByIndex(index + 1)
    }

    override fun getImgSrcByIndex(index: Int, yImageSlider: YImageSlider): String {
        return getImgByIndex((index + imgArray!!.size) % imgArray!!.size)
    }


    fun getImgSrcByIndex(index: Int): String {
        return getImgByIndex((index + imgArray!!.size) % imgArray!!.size)
    }

    companion object {
        private const val TAG = "PicContentActivity"
    }
}
