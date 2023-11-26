package com.example.jianming.services

import SERVER_IP
import SERVER_PORT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import com.example.jianming.Tasks.ConcurrencyImageTask
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.util.AppDataBase
import com.example.jianming.beans.SectionInfoBean
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.beans.PicSectionBean
import com.example.jianming.beans.UpdateStamp
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.myapplication.getSectionConfig
import com.example.jianming.util.TimeUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.nanjing.knightingal.processerlib.RefreshListener
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class DownloadService : Service() {

    private val binder: IBinder = LocalBinder();

    private lateinit var db: AppDataBase
    private lateinit var picSectionDao : PicSectionDao
    private lateinit var updataStampDao: UpdataStampDao
    private lateinit var picInfoDao : PicInfoDao

    val processCounter = hashMapOf<Long, Counter>()


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
        picSectionDao = db.picSectionDao()
        picInfoDao = db.picInfoDao()
        updataStampDao = db.updateStampDao()
    }

    public fun startDownloadSectionList() {
        val updateStamp = updataStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN") as UpdateStamp
        val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?time_stamp=${updateStamp.updateStamp}"
        Log.d("startDownloadWebPage", stringUrl)
        ConcurrencyJsonApiTask.startDownload(stringUrl) { allBody ->
            val mapper = jacksonObjectMapper()
            db.runInTransaction() {
                updateStamp.updateStamp = TimeUtil.currentTimeFormat()
                val picSectionBeanList: List<PicSectionBean> = mapper.readValue(allBody)
                updataStampDao.update(updateStamp)
                picSectionBeanList.forEach { picSectionDao.insert(it) }
            }

            val allPicSectionBeanList = picSectionDao.getAll().toList()
            refreshListener?.doRefreshList(allPicSectionBeanList)

            val pendingUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=PENDING"
            ConcurrencyJsonApiTask.startDownload(pendingUrl) { pendingBody ->
                val picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                if (picSectionBeanList.isNotEmpty()) {
                    picSectionBeanList.forEach {
                        picSectionDao.update(it)
                        val allIdList = allPicSectionBeanList.map{sectionBean -> sectionBean.id}.toList()
                        startDownloadSection(it.id, allIdList.indexOf(it.id));
                    }
                }
            }
            val localUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=LOCAL"
            ConcurrencyJsonApiTask.startDownload(localUrl) { pendingBody ->
                val picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                if (picSectionBeanList.isNotEmpty()) {
                    picSectionBeanList.forEach {
                        val existSection = picSectionDao.getByInnerIndex(it.id)
                        if (existSection.exist != 1) {
                            picSectionDao.update(it)
                            val allIdList = allPicSectionBeanList.map{sectionBean -> sectionBean.id}.toList()
                            startDownloadSection(it.id, allIdList.indexOf(it.id));
                        }
                    }
                }
            }
        }

    }

    fun startDownloadSection(index: Long, position: Int) {
        val url = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picContentAjax?id=$index"

        ConcurrencyJsonApiTask.startDownload(url) {body ->
            val picSectionBean = picSectionDao.getByInnerIndex(index)
            val mapper = jacksonObjectMapper()
            db.runInTransaction() {
                (mapper.readValue(body) as SectionInfoBean).pics.forEach { pic ->
                    val picInfoBean: PicInfoBean = PicInfoBean(
                        null,
                        pic,
                        picSectionBean.id,
                        null,
                        0,
                        0
                    )
                    picInfoDao.insert(picInfoBean)
                }
            }

            val picInfoBeanList = picInfoDao.queryBySectionInnerIndex(picSectionBean.id)
            processCounter[index] = Counter(picInfoBeanList.size)

            val sectionInfoBean = SectionInfoBean(
                "${picSectionBean.id}",
                picSectionBean.name,
                mutableListOf()
            )
            picInfoBeanList.forEach { picInfoBean ->
                sectionInfoBean.pics.add(picInfoBean.name)
                val picName = picInfoBean.name
                val sectionConfig = getSectionConfig(picSectionBean.album)

                var imgUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                        "/linux1000/${sectionConfig.baseUrl}/${sectionInfoBean.dirName}/${picName}"

                if (sectionConfig.encryped) {
                    imgUrl += ".bin"
                }
                val directory = getSectionStorageDir(applicationContext, sectionInfoBean.dirName)
                val file = File(directory, picName)
                ConcurrencyImageTask.downloadUrl(imgUrl, file, sectionConfig.encryped) { bytes ->

                    val currCount = processCounter[index]?.incProcess() as Int
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                    val width = options.outWidth
                    val height = options.outHeight
                    val absolutePath = file.absolutePath

                    picInfoBean.width = width
                    picInfoBean.height = height
                    picInfoBean.absolutePath = absolutePath

                    refreshListener?.doRefreshProcess(position, currCount, picInfoBeanList.size)

                    if (currCount == picInfoBeanList.size) {
                        db.runInTransaction() {
                            for (pic in picInfoBeanList) {
                                picInfoDao.update(pic)
                            }
                        }
                        processCounter.remove(index)
                        val completeUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                                "/local1000/completeSection?id=" + index
                        ConcurrencyJsonApiTask.startPost(completeUrl, "") {}
                    }

                }
            }
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): DownloadService {
            return this@DownloadService
        }
    }


}

private fun getSectionStorageDir(context: Context, sectionName: String): File {
    val externalFilesDirBase = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val file = File(externalFilesDirBase, sectionName)
    if (file.mkdirs()) {
        Log.i("KtDownloadService", "Directory of $file.absolutePath created")
    }
    return file
}

class Counter(val max: Int, ) {
    private val process: AtomicInteger = AtomicInteger(0)

    fun incProcess(): Int {
        return process.incrementAndGet()
    }

    fun getProcess() = process.get()
}
