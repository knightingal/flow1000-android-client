package com.example.jianming.services

import SERVER_IP
import SERVER_PORT
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.beans.ImgDetail
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.util.AppDataBase
import com.example.jianming.beans.PicSectionBean
import com.example.jianming.beans.PicSectionData
import com.example.jianming.beans.SectionInfoBean
import com.example.jianming.beans.UpdateStamp
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.myapplication.SectionConfig
import com.example.jianming.myapplication.getSectionConfig
import com.example.jianming.util.Decryptor
import com.example.jianming.util.FileUtil.getSectionStorageDir
import com.example.jianming.util.NetworkUtil
import com.example.jianming.util.TimeUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Request
import org.nanjing.knightingal.processerlib.RefreshListener
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class DownloadService : Service() {
    companion object {
        var refreshListener: MutableSet<RefreshListener> = mutableSetOf()
        var pendingSectionBeanList: MutableList<PicSectionData> = mutableListOf()
        val sectionThreadPool: ThreadPoolExecutor = ThreadPoolExecutor(2, 2, 30, TimeUnit.SECONDS,
            LinkedBlockingQueue())
        val imageThreadPool: ThreadPoolExecutor = ThreadPoolExecutor(10, 10, 30, TimeUnit.SECONDS,
            LinkedBlockingQueue())

    }

    private val binder: IBinder = LocalBinder()

    private lateinit var db: AppDataBase
    private lateinit var picSectionDao : PicSectionDao
    private lateinit var updateStampDao: UpdataStampDao
    private lateinit var picInfoDao : PicInfoDao



    fun setRefreshListener(refreshListener: RefreshListener?) {
        if (refreshListener != null) {
            DownloadService.refreshListener.add(refreshListener)
        }
    }


    fun removeRefreshListener(refreshListener: RefreshListener) {
        DownloadService.refreshListener.remove(refreshListener)
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
        updateStampDao = db.updateStampDao()
    }
    private var allPicSectionBeanList:List<PicSectionData> = listOf()


    fun fetchAllSectionList() {
        val updateStamp = updateStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN") as UpdateStamp
        val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?time_stamp=${updateStamp.updateStamp}"
        Log.d("startDownloadWebPage", stringUrl)
        ConcurrencyJsonApiTask.startGet(stringUrl) { allBody ->
            val mapper = jacksonObjectMapper()
            db.runInTransaction {
                updateStamp.updateStamp = TimeUtil.currentTimeFormat()
                val picSectionBeanList: List<PicSectionBean> = mapper.readValue(allBody)
                updateStampDao.update(updateStamp)
                picSectionBeanList.forEach { picSectionDao.insert(it) }
            }

            allPicSectionBeanList = picSectionDao.getAll().toList()
                .map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } }
            refreshListener.forEach {
                it.notifyListReady()
            }
        }

    }

    fun startDownloadSectionList() {
        thread {
            val mapper = jacksonObjectMapper()
            val updateStamp = updateStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN") as UpdateStamp
            val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?time_stamp=${updateStamp.updateStamp}"
            Log.d("startDownloadWebPage", stringUrl)

            val request = Request.Builder().url(stringUrl).build()

            val body = NetworkUtil.okHttpClient.newCall(request).execute().body.string()

            db.runInTransaction {
                updateStamp.updateStamp = TimeUtil.currentTimeFormat()
                val picSectionBeanList: List<PicSectionBean> = mapper.readValue(body)
                updateStampDao.update(updateStamp)
                picSectionBeanList.forEach { picSectionDao.insert(it) }
            }
            allPicSectionBeanList = picSectionDao.getAll().toList().map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } }
            val pendingUrl =
                "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=PENDING"

            val picIndexResp = NetworkUtil.okHttpClient.newCall(Request.Builder().url(pendingUrl).build()).execute().body.string()
            var picSectionBeanList: List<PicSectionBean> = mapper.readValue(picIndexResp)
            pendingSectionBeanList = mutableListOf()

            db.runInTransaction {
                picSectionBeanList = picSectionBeanList.filter {
                    val byServerIndex = picSectionDao.getByServerIndex(it.id)
                    byServerIndex.clientStatus != PicSectionBean.ClientStatus.LOCAL
                }
            }

            pendingSectionBeanList.addAll(picSectionBeanList.map { PicSectionData(it, 0).apply { this.process = 0 } })
            pendingSectionBeanList.sortBy { it.picSectionBean.id }
            db.runInTransaction {
                pendingSectionBeanList.forEach {
                    picSectionDao.updateClientStatusByServerIndex(
                        it.picSectionBean.id,
                        PicSectionBean.ClientStatus.PENDING
                    )
                }
            }
            MainScope().launch {
                refreshListener.forEach {
                    it.notifyListReady()
                }
            }
            pendingSectionBeanList.forEach { processPendingSectionItem(it) }
        }
    }


    private fun processPendingSectionItem(pendingSectionData: PicSectionData) {
        val clientStatus = picSectionDao.getByServerIndex(pendingSectionData.picSectionBean.id).clientStatus
        if (clientStatus == PicSectionBean.ClientStatus.LOCAL) {
            return
        }
        sectionThreadPool.execute {
            sectionExecuting(pendingSectionData)
        }
    }

    private fun processImgItem(pic: ImgDetail, sectionInfoBean: SectionInfoBean, sectionConfig: SectionConfig, latch: CountDownLatch) {
        val imgUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                "/linux1000/${sectionConfig.baseUrl}/${sectionInfoBean.dirName}/${if (sectionConfig.encryped) pic.name else pic.name}"
        imageThreadPool.execute {
            imgExecuting(imgUrl, pic, sectionInfoBean.id, sectionInfoBean.dirName, sectionConfig.encryped, latch)
        }
    }

    private fun imgExecuting(imgUrl: String, pic: ImgDetail, sectionId: Long, dirName: String, encrypted: Boolean, latch: CountDownLatch) {
        var bytes = NetworkUtil.okHttpClient.newCall(Request.Builder().url(imgUrl).build())
            .execute().body.bytes()
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        if (encrypted) {
            bytes = Decryptor.decrypt(bytes)
        }
        val directory =
            getSectionStorageDir(applicationContext, dirName)
        val dest = File(directory, pic.name)
        val fileOutputStream = FileOutputStream(dest, false)
        fileOutputStream.write(bytes)
        fileOutputStream.close()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        val width = options.outWidth
        val height = options.outHeight
        val absolutePath = dest.absolutePath
        val picInfoBean = PicInfoBean(
            pic.id,
            pic.name,
            sectionId,
            absolutePath,
            height,
            width,
        )
        picInfoDao.insert(picInfoBean)
        val currCounter = ProcessCounter.addCounter(sectionId)
        if (currCounter != null) {
            refreshListener.forEach {
                it.doRefreshProcess(
                    sectionId,
                    0,
                    currCounter.getProcess(),
                    currCounter.max
                )
            }
        }
        latch.countDown()
    }

    private fun sectionExecuting(pendingSectionData: PicSectionData) {
        val sectionConfig = getSectionConfig(pendingSectionData.picSectionBean.album)
        val url = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picDetailAjax?id=${pendingSectionData.picSectionBean.id}"
        val mapper = jacksonObjectMapper()
        val picContentResp = NetworkUtil.okHttpClient.newCall(Request.Builder().url(url).build()).execute().body.string()
        val sectionInfoBean = mapper.readValue<SectionInfoBean>(picContentResp)
        if (ProcessCounter.initCounter(pendingSectionData.picSectionBean.id, sectionInfoBean.pics.size) != null) {
            return
        }
        Log.d("DownloadService", "download section $url")
        picInfoDao.deleteBySectionInnerIndex(pendingSectionData.picSectionBean.id)
        val latch = CountDownLatch(sectionInfoBean.pics.size)
        sectionInfoBean.pics.forEach {
            processImgItem(it, sectionInfoBean, sectionConfig, latch)
        }
        latch.await()
        refreshListener.forEach { it.doRefreshProcess(pendingSectionData.picSectionBean.id, 0, sectionInfoBean.pics.size, sectionInfoBean.pics.size) }
        picSectionDao.updateClientStatusByServerIndex(
            pendingSectionData.picSectionBean.id,
            PicSectionBean.ClientStatus.LOCAL
        )
        ProcessCounter.remove(pendingSectionData.picSectionBean.id)
    }

    fun getPendingSectionList(): List<PicSectionData> {
        return pendingSectionBeanList.toList()
    }

    fun getAllSectionList(): List<PicSectionData> {
        return allPicSectionBeanList
    }

    inner class LocalBinder : Binder() {
        fun getService(): DownloadService {
            return this@DownloadService
        }
    }

}


