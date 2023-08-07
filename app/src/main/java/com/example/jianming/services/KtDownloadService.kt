package com.example.jianming.services

import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import androidx.room.Room
import com.example.jianming.Tasks.ConcurrencyImageTask
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.Tasks.DLAlbumTask
import com.example.jianming.Utils.AppDataBase
import com.example.jianming.Utils.EnvArgs
import com.example.jianming.beans.AlbumInfoBean
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.dao.PicAlbumDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.myapplication.getAlbumConfig
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.nanjing.knightingal.processerlib.RefreshListener
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class KtDownloadService : Service() {

    private val binder: IBinder = LocalBinder();

    private lateinit var db: AppDataBase
    private lateinit var picAlbumDao : PicAlbumDao
    private lateinit var picInfoDao : PicInfoDao

    val processCounter = hashMapOf<Int, Counter>()


    private var refreshListener: RefreshListener? = null

    fun setRefreshListener(refreshListener: RefreshListener) {
        this.refreshListener = refreshListener
    }

    fun removeRefreshListener() {
        this.refreshListener = null
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()
        picAlbumDao = db.picAlbumDao()
        picInfoDao = db.picInfoDao()
    }

    fun startDownloadAlbum(index: Long, position: Int) {
        val url = "http://${EnvArgs.serverIP}:${EnvArgs.serverPort}/local1000/picContentAjax?id=$index"

        ConcurrencyJsonApiTask.startDownload(url) {body ->
            val picAlbumBean = picAlbumDao.getByInnerIndex(index)
            val mapper = jacksonObjectMapper()
            try {
                val albumInfoBean: AlbumInfoBean = mapper.readValue(body)
                db.beginTransaction()
                albumInfoBean.pics.forEach { pic ->
                    val picInfoBean: PicInfoBean = PicInfoBean(
                        null,
                        pic,
                        picAlbumBean.id,
                        null,
                        0,
                        0
                    )
                    picInfoDao.insert(picInfoBean)

                }
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                db.endTransaction()
            }

            val picInfoBeanList = picInfoDao.queryByAlbumInnerIndex(picAlbumBean.id)
            processCounter[position] = Counter(picInfoBeanList.size)

            val albumInfoBean = AlbumInfoBean(
                "${picAlbumBean.id}",
                picAlbumBean.name,
                mutableListOf()
            )
            picInfoBeanList.forEach { picInfoBean ->
                albumInfoBean.pics.add(picInfoBean.name)
                val picName = picInfoBean.name
                val albumConfig = getAlbumConfig(picAlbumBean.album)

                var imgUrl = "http://${EnvArgs.serverIP}:${EnvArgs.serverPort}" +
                        "/linux1000/${albumConfig.baseUrl}/${albumInfoBean.dirName}/${picName}"

                if (albumConfig.encryped) {
                    imgUrl += ".bin"
                }
                val directory = DLAlbumTask.getAlbumStorageDir(applicationContext, albumInfoBean.dirName)
                val file = File(directory, picName)
                ConcurrencyImageTask.downloadUrl(imgUrl, file, albumConfig.encryped) { bytes ->

                    val currCount = processCounter[position]?.incProcess() as Int
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                    val width = options.outWidth
                    val height = options.outHeight
                    val absolutePath = file.absolutePath

                    picInfoBean.width = width
                    picInfoBean.height = height
                    picInfoBean.absolutePath = absolutePath

                    refreshListener?.doRefreshView(position, currCount, picInfoBeanList.size)

                    if (currCount == picInfoBeanList.size) {
                        try {
                            db.beginTransaction()
                            for (pic in picInfoBeanList) {
                                picInfoDao.update(pic)
                            }
                            db.setTransactionSuccessful()
                        } finally {
                            db.endTransaction()
                        }
                        processCounter.remove(position)
                    }

                }
            }
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): KtDownloadService {
            return this@KtDownloadService
        }
    }
}

class Counter(val max: Int) {
    private val process: AtomicInteger = AtomicInteger(0)

    fun incProcess(): Int {
        return process.incrementAndGet()
    }

    fun getProcess() = process.get()
}
