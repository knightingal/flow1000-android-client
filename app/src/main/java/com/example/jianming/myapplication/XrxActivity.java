package com.example.jianming.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jianming.views.YImageSlider;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import com.example.jianming.Utils.DIOptionsNoneScaled;


public class XrxActivity extends Activity implements YImageSlider.ImgChangeListener {

    private YImageSlider mImageSlider;

    private String[] imgs;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xrx);

        mImageSlider = (YImageSlider) findViewById(R.id.image);
        mImageSlider.setImgChangeListener(this);

//        String url = getIntent().getStringExtra("imgUrl");
//        String imageUrl;
//        if (url == null || url.equals("")) {
//            //String sdcard = Environment.getExternalStorageDirectory().getPath();
//            String imagePath = "/storage/sdcard1/BaiduNetdisk/xrx/[PureJapan]Vivian_Hsu/135.JPG";
//            //String imageUrl = ImageDownloader.Scheme.FILE.wrap(imagePath);
//            imageUrl = ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.su27long + "");
//        }
//        else {
//            imageUrl = url;
//        }
//        Log.d("onCreate", "imageUrl = " + imageUrl);
        imgs = getIntent().getStringArrayExtra("imgs");
        position = getIntent().getIntExtra("position", 0);
        DisplayImageOptions options = DIOptionsNoneScaled.getInstance().getOptions();
        if (imgs != null && imgs.length != 0) {
            index = position;
        }
        mImageSlider.setHideLeftSrc(index);
        mImageSlider.setContentSrc(index);
        mImageSlider.setHideRightSrc(index);
    }

    private String getImgByIndex(int index) {
        if (imgs != null) {
            if (index >= 0 && index < imgs.length) {
                return imgs[index];
            } else {
                return null;
            }
        } else {
            if (index >= 0 && index < pics.length) {
                return pics[index] + "";
            } else {
                return null;
            }
        }
    }

    int index = 0;
    int pics[] = {R.drawable.f14_1, R.drawable.f14_2, R.drawable.f14_3, R.drawable.f14_4,
//            R.drawable.f14_1, R.drawable.f14_2, R.drawable.f14_3, R.drawable.f14_4
    };

    public String onGetBackImg(YImageSlider yImageSlider) {
        index--;
        return getImgSrcByIndex(index - 1, yImageSlider);
    }

    public String onGetNextImg(YImageSlider yImageSlider) {
        index++;
        return getImgSrcByIndex(index + 1, yImageSlider);
    }

    @Override
    public String getImgSrcByIndex(int index, YImageSlider yImageSlider) {
        String img = getImgByIndex(index);
        if (img != null) {
            if (imgs != null) {
                return ImageDownloader.Scheme.FILE.wrap(img);
            } else {
                return ImageDownloader.Scheme.DRAWABLE.wrap(img);
            }
        } else {
            return null;
        }
    }
}
