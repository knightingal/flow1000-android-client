package com.example.jianming.beans;
import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.example.jianming.annotations.JsonName;

import java.util.List;

@Table(name = "T_ALBUM_INFO")
public class PicAlbumBean extends Model{
    public PicAlbumBean() {}

    public static List<PicAlbumBean> getAll() {
        return new Select().
                from(PicAlbumBean.class).
                orderBy(Cache.getTableInfo(PicAlbumBean.class).getIdName()).
                execute();
    }

    public static List<PicAlbumBean> getAllExist() {
        return new Select().
                from(PicAlbumBean.class).
                where("exist = ?", 1).
                orderBy(Cache.getTableInfo(PicAlbumBean.class).getIdName()).
                execute();
    }



    public static PicAlbumBean getByIndex(int index) {
        return new Select().
                from(PicAlbumBean.class).
                where("server_index = ?", index).
                executeSingle();
    }

    public static void deletePicAlbumFromDb(int index) {
        PicAlbumBean picAlbum = getByIndex(index);
        picAlbum.setExist(0).save();
        PicInfoBean.deleteByAlbum(picAlbum);
    }

    public static void setExistByIndex(int index, int exist) {
        getByIndex(index).setExist(exist).save();
    }

    public static int getExistByIndex(int index) {
        return getByIndex(index).getExist();
    }

    public PicAlbumBean(int index, String name) {
        this.index = index;
        this.name = name;
        this.exist = 0;
    }

    @JsonName("jsonName")
    @Column(name="Name")
    private String name;

    @JsonName("jsonIndex")
    @Column(name="server_index", index=true)
    private int index;

    @Column(name="exist")
    private int exist;

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


    public int getExist() {
        return exist;
    }

    public PicAlbumBean setExist(int exist) {
        this.exist = exist;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }
}
