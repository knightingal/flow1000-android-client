package com.example.jianming.myapplication

import android.app.Application
import androidx.room.Room.databaseBuilder
import com.example.jianming.beans.UpdateStamp
import com.example.jianming.services.TaskManager
import com.example.jianming.util.AppDataBase
import com.example.jianming.util.CrashHandler

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        TaskManager.applicationContext = applicationContext
        TaskManager.initForObserver(applicationContext)
        db = databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()
        initDB()
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
    }

    companion object {
        lateinit var db: AppDataBase
        public fun findDb(): AppDataBase {
            return db
        }
    }

    private fun initDB() {
        var sectionStamp: UpdateStamp? =
            db.updateStampDao().getUpdateStampByTableName("PIC_ALBUM_BEAN")
        if (sectionStamp == null) {
            sectionStamp = UpdateStamp()
            sectionStamp.tableName = "PIC_ALBUM_BEAN"
            sectionStamp.updateStamp = "20000101000000"
            db.updateStampDao().save(sectionStamp)
        }
    }
}
