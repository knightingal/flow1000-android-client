package com.example.jianming.beans;


import com.example.jianming.Utils.Daos;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Jianming on 2015/10/31.
 */

@Entity
public class PicInfoBean  {

    public static List<PicInfoBean> queryByAlbum(PicAlbumBean picAlbumBean) {
        return Daos.picInfoBeanDao.queryBuilder()
                .where(PicInfoBeanDao.Properties.AlbumIndex.eq(picAlbumBean.getInnerIndex()))
                .orderAsc(PicInfoBeanDao.Properties.Index)
                .list();
    }

    public static void deleteByAlbum(PicAlbumBean picAlbumBean) {
        Daos.picInfoBeanDao.queryBuilder()
                .where(PicInfoBeanDao.Properties.AlbumIndex.eq(picAlbumBean.getInnerIndex()))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

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

    @Id
    private Long index;

    private Long albumIndex;

    private String absolutePath;

    private int height;

    private int width;

    @Generated(hash = 966401127)
    public PicInfoBean(String name, Long index, Long albumIndex, String absolutePath, int height, int width) {
        this.name = name;
        this.index = index;
        this.albumIndex = albumIndex;
        this.absolutePath = absolutePath;
        this.height = height;
        this.width = width;
    }

    @Generated(hash = 540436994)
    public PicInfoBean() {
    }


}
