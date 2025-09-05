package org.knightingal.flow1000.client.myapplication

import android.app.Application
import androidx.room.Room.databaseBuilder
import org.knightingal.flow1000.client.beans.UpdateStamp
import org.knightingal.flow1000.client.util.AppDataBase
import org.knightingal.flow1000.client.util.CrashHandler

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

    companion object {
        lateinit var db: AppDataBase
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
