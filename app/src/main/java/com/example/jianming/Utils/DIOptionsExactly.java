package com.example.jianming.Utils;

import android.graphics.Bitmap;

import com.example.jianming.myapplication.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


public class DIOptionsExactly {
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.rockit)
            .showImageOnFail(R.drawable.ic_launcher)
            .cacheInMemory(false)
            .cacheOnDisk(false)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private DIOptionsExactly() {
    }

    public DisplayImageOptions getOptions() {
        return this.options;
    }
    private static DIOptionsExactly self = null;
    public static DIOptionsExactly getInstance() {
        if (self == null) {
            self = new DIOptionsExactly();
        }
        return self;
    }
}
