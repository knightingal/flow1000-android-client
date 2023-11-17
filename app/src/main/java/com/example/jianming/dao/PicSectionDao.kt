package com.example.jianming.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.jianming.beans.PicAlbumBean;


@Dao
interface PicSectionDao {

    @Query("select * from PicAlbumBean")
    fun getAll(): List<PicAlbumBean>

    @Update
    fun update( picAlbumData: PicAlbumBean)

    @Query("select * from PicAlbumBean where id = :index")
    fun getByInnerIndex(index: Long): PicAlbumBean

    @Query("select * from PicAlbumBean where id = :serverIndex")
    fun getByServerIndex(serverIndex: Long): PicAlbumBean

    @Query("select * from PicAlbumBean where exist = 1")
    fun getAllExist(): List<PicAlbumBean>

    @Insert
    fun insert(picAlbumBean: PicAlbumBean)

    @Delete
    fun deleteAll(picAlbumBeanList: List<PicAlbumBean>)

    @Delete
    fun delete(picAlbumBean: PicAlbumBean)
}
