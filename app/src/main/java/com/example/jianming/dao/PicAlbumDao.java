package com.example.jianming.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.jianming.beans.PicAlbumBean;

import java.util.List;

@Dao
public interface PicAlbumDao {

    @Query("select * from PicAlbumBean")
    List<PicAlbumBean> getAll();

    @Update
    void update(PicAlbumBean picAlbumData);

    @Query("select * from PicAlbumBean where innerIndex = :index")
    PicAlbumBean getByInnerIndex(int index);

    @Query("select * from PicAlbumBean where serverIndex = :serverIndex")
    PicAlbumBean getByServerIndex(int serverIndex);

    @Query("select * from PicAlbumBean where exist = 1")
    List<PicAlbumBean> getAllExist();

    @Insert
    void insert(PicAlbumBean picAlbumBean);

    @Delete
    void deleteAll(List<PicAlbumBean> picAlbumBeanList);

    @Delete
    void delete(PicAlbumBean picAlbumBean);
}
