package com.example.jianming.myapplication;

import com.example.jianming.views.DownloadProcessBar;

/**
 *
 *
 * @author Knightingal
 * @since v1.0
 */
public interface PicCompletedListener {

    /**
     * do pic list download complete
     * @param dirName the name of dir
     * @param index the serverIndex
     * @param localPosition position of local listView item
     */
    void doPicListDownloadComplete(String dirName, int index, int localPosition);


    /**
     * get download process bar by serverIndex
     * @param index the serverIndex
     * @param localPosition position of local listView item
     * @return the download process bar
     */
    DownloadProcessBar getDownloadProcessBarByIndex(int index, int localPosition);
}
