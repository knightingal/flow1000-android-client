package com.example.jianming.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.jianming.beans.UpdateStamp;


@Dao
interface UpdataStampDao {
    @Query("select * from UpdateStamp where table_name = :tableName")
    fun getUpdateStampByTableName(tableName: String):UpdateStamp?

    @Update
    fun update(updateStamp: UpdateStamp)

    @Insert
    fun save(sectionStamp: UpdateStamp)

    @Delete
    fun deleteAll(updateStampList: List<UpdateStamp>)
}
