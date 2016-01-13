package com.example.jianming.myapplication;

import com.example.jianming.views.DownloadProcessBar;

/**
 * Created by Knightingal on 2015/12/28.
 */
public interface PicCompletedListener {
    void doPicListDownloadComplete(String dirName, int index);

    DownloadProcessBar getDownloadProcessBarByIndex(int index);
}
