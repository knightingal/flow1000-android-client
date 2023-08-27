package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.nostra13.universalimageloader.core.download.ImageDownloader;

import org.nanking.knightingal.kslideviewlib.YImageSlider;


public class PicContentActivity extends Activity implements YImageSlider.ImgChangeListener {

    private static final String TAG = "PicContentActivity";

//    @BindView(R.id.image)
    public YImageSlider mImageSlider;

    private String[] imgArray;

    private int position;



    @Override
    protected void onResume() {
        super.onResume();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_content);
        mImageSlider = findViewById(R.id.image);

//        ButterKnife.bind(this);

        mImageSlider.setImgChangeListener(this);
        imgArray = getIntent().getStringArrayExtra("imgArray");
        position = getIntent().getIntExtra("position", 0);
        if (imgArray != null && imgArray.length != 0) {
            index = position;
        }
        mImageSlider.setHideLeftSrc(index);
        mImageSlider.setContentSrc(index);
        mImageSlider.setHideRightSrc(index);
    }

    private String getImgByIndex(int index) {
        if (imgArray != null) {
            if (index >= 0 && index < imgArray.length) {
                return imgArray[index];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    int index = 0;


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("position", index);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @NonNull
    @Override
    public String onGetBackImg(@NonNull YImageSlider yImageSlider) {
        index--;
        return getImgSrcByIndex(index - 1);
    }

    @NonNull
    @Override
    public String onGetNextImg(@NonNull YImageSlider yImageSlider) {
        index++;
        return getImgSrcByIndex(index + 1);
    }

    @NonNull
    @Override
    public String getImgSrcByIndex(int index, @NonNull YImageSlider yImageSlider) {
        String img = getImgByIndex(index);
        return img;
//        Log.d(TAG, "getImgSrcByIndex " + index + " " + img);
//        if (img != null) {
//            if (imgArray != null) {
//                return ImageDownloader.Scheme.FILE.wrap(img);
//            } else {
//                return ImageDownloader.Scheme.DRAWABLE.wrap(img);
//            }
//        } else {
//            return null;
//        }
    }


    public String getImgSrcByIndex(int index) {
        String img = getImgByIndex(index);
        return img;
//        Log.d(TAG, "getImgSrcByIndex " + index + " " + img);
//        if (img != null) {
//            if (imgArray != null) {
//                return ImageDownloader.Scheme.FILE.wrap(img);
//            } else {
//                return ImageDownloader.Scheme.DRAWABLE.wrap(img);
//            }
//        } else {
//            return null;
//        }
    }
}
