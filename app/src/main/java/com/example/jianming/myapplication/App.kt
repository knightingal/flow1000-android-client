package com.example.jianming.myapplication

import android.app.Application
import androidx.room.Room.databaseBuilder
import com.example.jianming.beans.UpdateStamp
import com.example.jianming.util.AppDataBase
import com.example.jianming.util.CrashHandler

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        db = databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()
        initDB()
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
    }

    lateinit var db: AppDataBase

    private fun initDB() {
        var sectionStamp: UpdateStamp? =
            this.db.updateStampDao().getUpdateStampByTableName("PIC_ALBUM_BEAN")
        if (sectionStamp == null) {
            sectionStamp = UpdateStamp()
            sectionStamp.tableName = "PIC_ALBUM_BEAN"
            sectionStamp.updateStamp = "20000101000000"
            this.db.updateStampDao().save(sectionStamp)
        }
    }
}
