package com.example.jianming.beans;
import com.example.jianming.Utils.Daos;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PicAlbumBean {
    public static List<PicAlbumBean> getAll() {

        return Daos.picAlbumBeanDao.queryBuilder()
                .orderAsc(PicAlbumBeanDao.Properties.Name)
                .list();
    }

    public static List<PicAlbumBean> getAllExist() {

          return Daos.picAlbumBeanDao.queryBuilder()
                    .where(PicAlbumBeanDao.Properties.Exist.eq(1))
                    .orderAsc(PicAlbumBeanDao.Properties.Name)
                    .list();
    }



    public static PicAlbumBean getByServerIndex(int serverIndex) {
        return Daos.picAlbumBeanDao.queryBuilder()
                .where(PicAlbumBeanDao.Properties.ServerIndex.eq(serverIndex))
                .unique();
    }

    public static PicAlbumBean getByInnerIndex(int index) {
        return Daos.picAlbumBeanDao.queryBuilder()
                .where(PicAlbumBeanDao.Properties.InnerIndex.eq(index))
                .unique();
    }

    public static void deletePicAlbumFromDb(int serverIndex) {
        Daos.picAlbumBeanDao.delete(getByServerIndex(serverIndex));
    }

    public static void setExistByServerIndex(int serverIndex, int exist) {
        PicAlbumBean picAlbumBean = getByInnerIndex(serverIndex);
        picAlbumBean.setExist(exist);
        Daos.picAlbumBeanDao.update(picAlbumBean);
    }

    public static int getExistByServerIndex(int serverIndex) {
        return getByServerIndex(serverIndex).getExist();
    }

    public PicAlbumBean(int serverIndex, String name) {
        this.serverIndex = serverIndex;
        this.name = name;
        this.exist = 0;
    }

    @Generated(hash = 741092784)
    public PicAlbumBean(String name, int serverIndex, int exist, Long innerIndex,
            String mtime) {
        this.name = name;
        this.serverIndex = serverIndex;
        this.exist = exist;
        this.innerIndex = innerIndex;
        this.mtime = mtime;
    }

    @Generated(hash = 1417899696)
    public PicAlbumBean() {
    }

    @JsonProperty("name")
    private String name;

    @JsonProperty("index")
    private int serverIndex;

    private int exist;

    @Id
    private Long innerIndex;


    private String mtime;

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

    public PicAlbumBean setExist(int exist) {
        this.exist = exist;
        return this;
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
}
