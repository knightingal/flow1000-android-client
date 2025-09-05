package org.knightingal.flow1000.client.util

import androidx.room.Database
import androidx.room.RoomDatabase
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.beans.PicInfoBean
import org.knightingal.flow1000.client.beans.UpdateStamp
import org.knightingal.flow1000.client.dao.PicSectionDao
import org.knightingal.flow1000.client.dao.PicInfoDao
import org.knightingal.flow1000.client.dao.UpdataStampDao

@Database(entities = [UpdateStamp::class, PicSectionBean::class, PicInfoBean::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun picSectionDao(): PicSectionDao

    abstract fun picInfoDao(): PicInfoDao

    abstract fun updateStampDao(): UpdataStampDao
}