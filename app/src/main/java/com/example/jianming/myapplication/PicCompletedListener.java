package com.example.jianming.myapplication;

import com.example.jianming.views.DownloadProcessBar;

/**
 * Created by Knightingal on 2015/12/28.
 */
public interface PicCompletedListener {

    /**
     * do pic list download complete
     * @param dirName the name of dir
     * @param index the serverIndex
     */
    void doPicListDownloadComplete(String dirName, int index);


    /**
     * get download process bar by serverIndex
     * @param index the serverIndex
     * @return the download process bar
     */
    DownloadProcessBar getDownloadProcessBarByIndex(int index);
}
