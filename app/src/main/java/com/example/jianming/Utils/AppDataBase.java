package com.example.jianming.Utils;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;
import com.example.jianming.beans.UpdateStamp;
import com.example.jianming.dao.PicAlbumDao;
import com.example.jianming.dao.PicInfoDao;
import com.example.jianming.dao.UpdataStampDao;

@Database(entities = {PicAlbumBean.class, PicInfoBean.class, UpdateStamp.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract PicAlbumDao picAlbumDao();

    public abstract PicInfoDao picInfoDao();

    public abstract UpdataStampDao updataStampDao();
}
