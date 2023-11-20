package com.example.jianming.util

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jianming.beans.PicSectionBean
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.beans.UpdateStamp
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.UpdataStampDao

@Database(entities = [UpdateStamp::class, PicSectionBean::class, PicInfoBean::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun picSectionDao(): PicSectionDao

    abstract fun picInfoDao(): PicInfoDao

    abstract fun updateStampDao(): UpdataStampDao
}