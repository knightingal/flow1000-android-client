package com.example.jianming.Utils;

import android.graphics.Bitmap;

import com.example.jianming.myapplication.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by Jianming on 2015/3/15.
 */
public class DIOptions {
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.rockit)
            .showImageOnFail(R.drawable.ic_launcher)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private DIOptions() {

    }

    public DisplayImageOptions getOptions() {
        return this.options;
    }
    private static DIOptions self = null;
    public static DIOptions getInstance() {
        if (self == null) {
            self = new DIOptions();
        }
        return self;
    }
}
