package com.example.jianming.beans;
import com.example.jianming.annotations.JsonName;


public class PicIndexBean {
    public PicIndexBean() {}

    public PicIndexBean(int index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonName("jsonName")
    private String name;

    @JsonName("jsonIndex")
    private int index;

//    @JsonName("jsonMtime")
//    private String mtime;

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

//    public String getMtime() {
//        return mtime;
//    }
//
//    public void setMtime(String mtime) {
//        this.mtime = mtime;
//    }

    public void setName(String name) {
        this.name = name;
    }
}
