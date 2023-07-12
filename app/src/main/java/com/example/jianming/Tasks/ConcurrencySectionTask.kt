package com.example.jianming.Tasks

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import androidx.room.Room.databaseBuilder
import com.example.jianming.Utils.AppDataBase
import com.example.jianming.Utils.EnvArgs
import com.example.jianming.beans.AlbumInfoBean
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.dao.PicAlbumDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.myapplication.getAlbumConfig
import org.nanjing.knightingal.processerlib.TaskNotifier
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class ConcurrencySectionTask  {
//    private final val TAG = "DLAlbumTask"

    private val position: Int

    private val context: Context

    private val db: AppDataBase

    private val picAlbumDao: PicAlbumDao

    private val picInfoDao: PicInfoDao

    private lateinit var picInfoBeanList: List<PicInfoBean>

    private var taskNotifier: TaskNotifier

    constructor(context: Context, position: Int, taskNotifier: TaskNotifier) {

        this.position = position
        this.context = context
        db = databaseBuilder<AppDataBase>(
            context,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()
        picAlbumDao = db.picAlbumDao()
        picInfoDao = db.picInfoDao()
        this.taskNotifier = taskNotifier
    }


    public fun asyncStartDownload(index: Long) : Unit {
        val picAlbumBean = picAlbumDao.getByInnerIndex(index)
        picInfoBeanList = picInfoDao.queryByAlbumInnerIndex(picAlbumBean.id)
        val albumInfoBean = AlbumInfoBean(
            "${picAlbumBean.id}",
            picAlbumBean.name,
            mutableListOf()
        )
        picInfoBeanList.forEach { picInfoBean ->
            albumInfoBean.pics.add(picInfoBean.name)
            val picName = picInfoBean.name
            val albumConfig = getAlbumConfig(picAlbumBean.album)
            var url = "http://${EnvArgs.serverIP}:${EnvArgs.serverPort}" +
                    "/linux1000/${albumConfig.baseUrl}/${albumInfoBean.dirName}/${picName}"

            if (albumConfig.encryped) {
                url = url + ".bin"
            }
            val directory = DLAlbumTask.getAlbumStorageDir(context, albumInfoBean.dirName)
            val file = File(directory, picName)

            ConcurrencyImageTask.downloadUrl(url, file, albumConfig.encryped) {bytes ->
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                val width = options.outWidth
                val height = options.outHeight
                val absolutePath = file.absolutePath

                updatePicInfoBean(picInfoBeanList.indexOf(picInfoBean), width, height, absolutePath)

                taskNotifier.onTaskComplete(position, processCount.get(), picInfoBeanList.size)


            }
        }

    }

    private val processCount = AtomicInteger(0)

    private fun updatePicInfoBean(index: Int, width: Int, height: Int, path: String) {
        picInfoBeanList.get(index).width = width
        picInfoBeanList.get(index).height = height
        picInfoBeanList.get(index).absolutePath = path
        val currCount: Int = processCount.incrementAndGet()
        if (currCount == picInfoBeanList.size) {
            try {
                db.beginTransaction()
                for (picInfoBean in picInfoBeanList) {
                    picInfoDao.update(picInfoBean!!)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }


}