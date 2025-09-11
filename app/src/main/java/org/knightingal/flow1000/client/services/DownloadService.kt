package org.knightingal.flow1000.client.services

import org.knightingal.flow1000.client.util.SERVER_IP
import org.knightingal.flow1000.client.util.SERVER_PORT
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import org.knightingal.flow1000.client.task.ConcurrencyJsonApiTask
import org.knightingal.flow1000.client.beans.ImgDetail
import org.knightingal.flow1000.client.beans.PicInfoBean
import org.knightingal.flow1000.client.util.AppDataBase
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.beans.PicSectionData
import org.knightingal.flow1000.client.beans.SectionInfoBean
import org.knightingal.flow1000.client.beans.UpdateStamp
import org.knightingal.flow1000.client.dao.PicSectionDao
import org.knightingal.flow1000.client.dao.PicInfoDao
import org.knightingal.flow1000.client.dao.UpdateStampDao
import org.knightingal.flow1000.client.myapplication.SectionConfig
import org.knightingal.flow1000.client.util.Decrypt
import org.knightingal.flow1000.client.util.FileUtil.getSectionStorageDir
import org.knightingal.flow1000.client.util.TimeUtil
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.knightingal.flow1000.client.myapplication.SectionConfig.Companion.getSectionConfig
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
    private lateinit var updateStampDao: UpdateStampDao
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
//            val mapper = jacksonObjectMapper()
            db.runInTransaction {
                updateStamp.updateStamp = TimeUtil.currentTimeFormat()
                val picSectionBeanList = Gson().fromJson(allBody, Array<PicSectionBean>::class.java)
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
//            val mapper = jacksonObjectMapper()
            val updateStamp = updateStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN") as UpdateStamp
            val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?time_stamp=${updateStamp.updateStamp}"
            Log.d("startDownloadWebPage", stringUrl)

            val client = HttpClient(CIO)
            val body: String = runBlocking {
                val response: HttpResponse = client.get(stringUrl)
                response.body()
            }

            db.runInTransaction {
                updateStamp.updateStamp = TimeUtil.currentTimeFormat()
                val picSectionBeanList = Gson().fromJson(body, Array<PicSectionBean>::class.java)
                updateStampDao.update(updateStamp)
                picSectionBeanList.forEach { picSectionDao.insert(it) }
            }
            allPicSectionBeanList = picSectionDao.getAll().toList().map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } }
            val pendingUrl =
                "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=PENDING"
            val picIndexResp: String = runBlocking {
                val response: HttpResponse = client.get(pendingUrl)
                response.body()
            }

            var picSectionBeanList = Gson().fromJson(picIndexResp, Array<PicSectionBean>::class.java).toList()
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
                "/linux1000/${sectionConfig.baseUrl}/${sectionInfoBean.dirName}/${if (sectionConfig.encrypted) pic.name else pic.name}"
        imageThreadPool.execute {
            imgExecuting(imgUrl, pic, sectionInfoBean.id, sectionInfoBean.dirName, sectionConfig.encrypted, latch)
        }
    }

    private fun imgExecuting(imgUrl: String, pic: ImgDetail, sectionId: Long, dirName: String, encrypted: Boolean, latch: CountDownLatch) {

        val client = HttpClient(OkHttp)
        var bytes: ByteArray = runBlocking {
            val response: HttpResponse = client.get(imgUrl)
            response.body()
        }
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        if (encrypted) {
            bytes = Decrypt.decrypt(bytes)
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
                     false
                )
            }
        }
        latch.countDown()
    }

    private fun sectionExecuting(pendingSectionData: PicSectionData) {
        val sectionConfig = getSectionConfig(pendingSectionData.picSectionBean.album)
        val url = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picDetailAjax?id=${pendingSectionData.picSectionBean.id}"
//        val mapper = jacksonObjectMapper()
        val client = HttpClient(CIO)
        val picContentResp: String = runBlocking {
            client.get(url).body()
        }
        val sectionInfoBean = Gson().fromJson(picContentResp, SectionInfoBean::class.java)
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
        refreshListener.forEach { it.doRefreshProcess(pendingSectionData.picSectionBean.id, true) }
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


