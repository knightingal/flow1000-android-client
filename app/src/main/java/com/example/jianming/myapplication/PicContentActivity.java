package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.nostra13.universalimageloader.core.download.ImageDownloader;

import org.nanking.knightingal.view.ImgChangeListener;
import org.nanking.knightingal.view.KImageSlider;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PicContentActivity extends Activity implements ImgChangeListener {

    private static final String TAG = "PicContentActivity";

    @BindView(R.id.image)
    public KImageSlider mImageSlider;

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

        ButterKnife.bind(this);

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

    @Override
    public String onGetBackImg() {
        index--;
        return getImgSrcByIndex(index - 1);
    }

    @Override
    public String onGetNextImg() {
        index++;
        return getImgSrcByIndex(index + 1);
    }

    @Override
    public String getImgSrcByIndex(int index) {
        String img = getImgByIndex(index);
        Log.d(TAG, "getImgSrcByIndex " + index + " " + img);
        if (img != null) {
            if (imgArray != null) {
                return ImageDownloader.Scheme.FILE.wrap(img);
            } else {
                return ImageDownloader.Scheme.DRAWABLE.wrap(img);
            }
        } else {
            return null;
        }
    }
}
