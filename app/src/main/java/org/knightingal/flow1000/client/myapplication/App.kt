package org.knightingal.flow1000.client.myapplication

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.room.Room.databaseBuilder
import coil3.ImageLoader
import coil3.PlatformContext
import org.knightingal.flow1000.client.beans.UpdateStamp
import org.knightingal.flow1000.client.util.AppDataBase
import org.knightingal.flow1000.client.util.CrashHandler
import java.io.File
import coil3.SingletonImageLoader
import com.github.awxkee.avifcoil.decoder.HeifDecoder

class App : Application(), SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(HeifDecoder.Factory())
            }
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        db = databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()
        initDB()
        clearDownloadPath()
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
    }

    companion object {
        lateinit var db: AppDataBase
    }

    private fun clearDownloadPath() {
        Log.i(App::class.java.simpleName, "start clearDownloadPath")
        val directory = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "apk")
        if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.forEach {
                Log.i(App::class.java.simpleName, "${it.name} deleted")
                it.delete()
            }
        }
        Log.i(App::class.java.simpleName, "finish clearDownloadPath")
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
