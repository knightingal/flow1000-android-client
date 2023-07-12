package com.example.jianming.dao;


import androidx.room.Dao;
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

    @Update
    fun update(picInfoBean: PicInfoBean);
}
