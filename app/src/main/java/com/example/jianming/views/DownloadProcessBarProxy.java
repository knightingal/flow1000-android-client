package com.example.jianming.views;

/**
 * Created by Knightingal on 2016/1/13.
 */
public class DownloadProcessBarProxy {

    public void clear() {
        currCount = 0;
        length = 0;
    }

    public void longer() {
        currCount++;
        length = this.width * currCount / this.stepCount;
    }
    private int stepCount = 0;

    private int currCount = 0;

    private int width = 0;

    private int length = 0;
}
