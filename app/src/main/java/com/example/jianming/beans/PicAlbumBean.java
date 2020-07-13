package com.example.jianming.beans;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class PicAlbumBean {


    @JsonProperty("name")
    private String name;

    @JsonProperty("index")
    private int serverIndex;

    private int exist;

    @PrimaryKey
    private Long innerIndex;


    private String mtime;

    private String cover;

    private int coverWidth;

    private int coverHeight;

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public String getName() {
        return name;
    }

    public int getServerIndex() {
        return serverIndex;
    }

    public void setServerIndex(int serverIndex) {
        this.serverIndex = serverIndex;
    }

    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setInnerIndex(Long innerIndex) {
        this.innerIndex = innerIndex;
    }

    public Long getInnerIndex() {
        return this.innerIndex;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getCoverWidth() {
        return coverWidth;
    }

    public void setCoverWidth(int coverWidth) {
        this.coverWidth = coverWidth;
    }

    public int getCoverHeight() {
        return coverHeight;
    }

    public void setCoverHeight(int coverHeight) {
        this.coverHeight = coverHeight;
    }
}
