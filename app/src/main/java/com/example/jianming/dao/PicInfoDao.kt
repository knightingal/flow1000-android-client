package com.example.jianming.dao;


import androidx.room.Dao;
import androidx.room.Delete
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.jianming.beans.PicInfoBean;



@Dao
interface PicInfoDao {

    @Insert
    fun insert(picInfoBean: PicInfoBean)

    @Query("select * from PicInfoBean where albumIndex = :innerIndex")
    fun queryByAlbumInnerIndex(innerIndex: Long):List<PicInfoBean>

    @Query("delete from PicInfoBean where albumIndex = :innerIndex")
    fun deleteByAlbumInnerIndex(innerIndex: Long)

    @Update
    fun update(picInfoBean: PicInfoBean);
}
