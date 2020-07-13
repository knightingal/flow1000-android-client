package com.example.jianming.beans;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;


/**
 * Created by Jianming on 2015/10/31.
 */

@Entity
public class PicInfoBean  {

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getIndex() {
        return this.index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getAlbumIndex() {
        return this.albumIndex;
    }

    public void setAlbumIndex(Long albumIndex) {
        this.albumIndex = albumIndex;
    }

    public String getAbsolutePath() {
        return this.absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private String name;

    @PrimaryKey
    private Long index;

    private Long albumIndex;

    private String absolutePath;

    private int height;

    private int width;




}
