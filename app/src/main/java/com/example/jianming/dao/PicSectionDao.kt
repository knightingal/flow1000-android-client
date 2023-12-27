package com.example.jianming.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.jianming.beans.PicSectionBean;


@Dao
interface PicSectionDao {

    @Query("select * from PicSectionBean")
    fun getAll(): List<PicSectionBean>

    @Update
    fun update(picSectionData: PicSectionBean)

    @Query("select * from PicSectionBean where id = :index")
    fun getByInnerIndex(index: Long): PicSectionBean

    @Query("select * from PicSectionBean where id = :serverIndex")
    fun getByServerIndex(serverIndex: Long): PicSectionBean

    @Query("select * from PicSectionBean where exist = 1")
    fun getAllExist(): List<PicSectionBean>

    @Insert
    fun insert(picSectionBean: PicSectionBean)

    @Delete
    fun deleteAll(picSectionBeanList: List<PicSectionBean>)

    @Delete
    fun delete(picSectionBean: PicSectionBean)

    @Query("Update PicSectionBean set clientStatus=:status where id=:serverIndex")
    fun updateClientStatusByServerIndex(serverIndex: Long, status: PicSectionBean.ClientStatus)
}
