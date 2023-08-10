package com.example.jianming.Utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jianming.beans.PicAlbumBean
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.beans.UpdateStamp
import com.example.jianming.dao.PicAlbumDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.UpdataStampDao

@Database(entities = [UpdateStamp::class, PicAlbumBean::class, PicInfoBean::class], version = 2)
abstract class AppDataBase : RoomDatabase() {

    abstract fun picAlbumDao(): PicAlbumDao

    abstract fun picInfoDao(): PicInfoDao

    abstract fun updateStampDao(): UpdataStampDao
}