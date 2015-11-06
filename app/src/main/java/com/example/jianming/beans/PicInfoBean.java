package com.example.jianming.beans;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Jianming on 2015/10/31.
 */

@Table(name = "T_PIC_INFO")
public class PicInfoBean extends Model {

    public static List<PicInfoBean> queryByAlbum(PicAlbumBean picAlbumBean) {
        return new Select().from(PicInfoBean.class).where("album_info = ?", picAlbumBean.getId()).
                orderBy("pic_index").execute();
    }

    @Column(name="pic_name")
    private String name;

    @Column(name="pic_index")
    private int index;

    @Column(name="album_info")
    private PicAlbumBean albumInfo;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    @Column(name="absolute_path")
    private String absolutePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PicAlbumBean getAlbumInfo() {
        return albumInfo;
    }

    public void setAlbumInfo(PicAlbumBean albumInfo) {
        this.albumInfo = albumInfo;
    }
}
