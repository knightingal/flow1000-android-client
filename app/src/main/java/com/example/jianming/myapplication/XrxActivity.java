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
            index = position - 1;
            ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.FILE.wrap(imgs[index]), mImageSlider.getHideLeft(), options);
            ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.FILE.wrap(imgs[index + 1]), mImageSlider.getContentView(), options);
            ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.FILE.wrap(imgs[index + 2]), mImageSlider.getHideRight(), options);
        } else {
            ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index] + ""), mImageSlider.getHideLeft(), options);
            ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index + 1] + ""), mImageSlider.getContentView(), options);
            ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(pics[index + 2] + ""), mImageSlider.getHideRight(), options);
        }
    }

    int index = 0;
    int pics[] = {R.drawable.f14_1, R.drawable.f14_2, R.drawable.f14_3, R.drawable.f14_4,
            R.drawable.f14_1, R.drawable.f14_2, R.drawable.f14_3, R.drawable.f14_4};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_xrx, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.show_pic_size) {
//            Toast.makeText(this, mImageContentView.picSize(), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public String onGetBackImg(YImageSlider yImageSlider) {
        index--;

        if (imgs != null && imgs.length != 0) {
            if (index < 0) {
                index = imgs.length - 1;
            }
            return ImageDownloader.Scheme.FILE.wrap(imgs[index] + "");
        } else {
            if (index < 0) {
                index = 3;
            }
            return ImageDownloader.Scheme.DRAWABLE.wrap(pics[index] + "");
        }
    }

    public String onGetNextImg(YImageSlider yImageSlider) {
        index++;

        if (imgs != null && imgs.length != 0) {
            if (index == imgs.length) {
                index = 0;
            }
            return ImageDownloader.Scheme.FILE.wrap(imgs[index + 2] + "");
        } else {
            if (index > 3) {
                index = 0;
            }
            return ImageDownloader.Scheme.DRAWABLE.wrap(pics[index + 2] + "");
        }
    }
}
