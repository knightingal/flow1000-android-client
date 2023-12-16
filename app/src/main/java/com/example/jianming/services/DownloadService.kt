package com.example.jianming.services

import SERVER_IP
import SERVER_PORT
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.workDataOf
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.Tasks.DownloadCompleteWorker
import com.example.jianming.Tasks.DownloadImageWorker
import com.example.jianming.Tasks.DownloadSectionWorker
import com.example.jianming.util.AppDataBase
import com.example.jianming.beans.PicSectionBean
import com.example.jianming.beans.PicSectionData
import com.example.jianming.beans.UpdateStamp
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.myapplication.getSectionConfig
import com.example.jianming.util.TimeUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.nanjing.knightingal.processerlib.RefreshListener
import java.util.concurrent.atomic.AtomicInteger

class DownloadService : Service() {

    private val binder: IBinder = LocalBinder();

    private lateinit var db: AppDataBase
    private lateinit var picSectionDao : PicSectionDao
    private lateinit var updateStampDao: UpdataStampDao
    private lateinit var picInfoDao : PicInfoDao

    val processCounter = hashMapOf<Long, Counter>()

    private var refreshListener: RefreshListener? = null

    fun setRefreshListener(refreshListener: RefreshListener?) {
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
        updateStampDao = db.updateStampDao()
    }
    private lateinit var allPicSectionBeanList:List<PicSectionBean>
    fun startDownloadSectionList() {
        val updateStamp = updateStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN") as UpdateStamp
        val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?time_stamp=${updateStamp.updateStamp}"
        Log.d("startDownloadWebPage", stringUrl)
        ConcurrencyJsonApiTask.startDownload(stringUrl) { allBody ->
            val mapper = jacksonObjectMapper()
            db.runInTransaction() {
                updateStamp.updateStamp = TimeUtil.currentTimeFormat()
                val picSectionBeanList: List<PicSectionBean> = mapper.readValue(allBody)
                updateStampDao.update(updateStamp)
                picSectionBeanList.forEach { picSectionDao.insert(it) }
            }

            allPicSectionBeanList = picSectionDao.getAll().toList()
//            startWork(3L)
//            startWork(4L)
//            startWork(30L)
//            startWork(31L)
//            viewWork()

            if (true) {
                val pendingUrl =
                    "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=PENDING"
                ConcurrencyJsonApiTask.startDownload(pendingUrl) { pendingBody ->
                    val picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                    if (picSectionBeanList.isNotEmpty()) {
                        picSectionBeanList.forEach {
                            picSectionDao.update(it)
//                            val allIdList =
//                                allPicSectionBeanList.map { sectionBean -> sectionBean.id }.toList()
                            startWork(it.id );
                        }
                    }
                    viewWork()
                }
                val localUrl =
                    "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=LOCAL"
                ConcurrencyJsonApiTask.startDownload(localUrl) { pendingBody ->
                    val picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                    if (picSectionBeanList.isNotEmpty()) {
                        picSectionBeanList.forEach {
                            val existSection = picSectionDao.getByInnerIndex(it.id)
                            if (existSection.exist != 1) {
                                picSectionDao.update(it)
//                                val allIdList =
//                                    allPicSectionBeanList.map { sectionBean -> sectionBean.id }
//                                        .toList()
                                startWork(it.id);
                            }
                        }
                        viewWork()
                    }
                }
            }

            refreshListener?.notifyListReady()
        }

    }

    private var pendingSectionBeanList: MutableList<PicSectionData> = mutableListOf()

    fun startDownloadSection(index: Long, position: Int) {
        refreshListener?.notifyListReady()
    }

    private fun getRefreshListener(): RefreshListener? {
        return refreshListener
    }

    fun getPendingSectionList(): List<PicSectionData> {
        return pendingSectionBeanList.toList()
    }

    inner class LocalBinder : Binder() {
        fun getService(): DownloadService {
            return this@DownloadService
        }
    }


    private fun startWork(sectionId: Long) {

        val picSectionBean = picSectionDao.getByInnerIndex(sectionId)

        val sectionConfig = getSectionConfig(picSectionBean.album)

        val workQuery: WorkQuery = WorkQuery.fromUniqueWorkNames("downloadTaskHeader:${sectionId}")
        val existHeaderWorker = WorkManager.getInstance(this).getWorkInfos(workQuery).get()
        if (existHeaderWorker.size > 0) {
            Log.i("DownloadService", "downloadTaskHeader:${sectionId} exist, skip to create worker")
            return
        }
        val downloadSectionRequest = OneTimeWorkRequestBuilder<DownloadSectionWorker>()
            .addTag("sectionStart")
            .addTag("sectionId:${sectionId}")
            .setInputData(workDataOf(
                DownloadSectionWorker.PARAM_SECTION_ID_KEY to sectionId
            )).build()
        WorkManager.getInstance(this).enqueueUniqueWork("downloadTaskHeader:${sectionId}",
            ExistingWorkPolicy.KEEP,
            downloadSectionRequest
        )
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(downloadSectionRequest.id)
            .observeForever() { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    val totalImageCount = workInfo.outputData.getInt(DownloadSectionWorker.TOTAL_IMAGE_COUNT_KEY, 0)
                    Log.d("DownloadService", "worker for $sectionId finish, totalCount=${totalImageCount}")
                    val dirName = workInfo.outputData.getString(DownloadSectionWorker.PARAM_DIR_NAME_KEY) as String
                    val sectionBeanId = workInfo.outputData.getLong(DownloadSectionWorker.PARAM_SECTION_BEAN_ID_KEY, 0)
                    val picInfoList =
                        picInfoDao.queryBySectionInnerIndex(sectionBeanId)

                    val imgWorkerList = picInfoList.map { pic ->
                        val picName = pic.name

                        val imgUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                                "/linux1000/${sectionConfig.baseUrl}/${dirName}/${if (sectionConfig.encryped) "$picName.bin" else picName}"

                        OneTimeWorkRequestBuilder<DownloadImageWorker>()
                            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                            .addTag(imgUrl)
                            .addTag("sectionId:${sectionId}:image")
                            .setInputData(workDataOf("imgUrl" to imgUrl,
                                "picId" to pic.index,
                                "picName" to picName,
                                "dirName" to dirName,
                                "sectionBeanId" to picSectionBean.id,
                                "encrypted" to sectionConfig.encryped,
                            ))
                            .build()
                    }

                    val beginWith = WorkManager.getInstance(this).beginUniqueWork(
                        "sectionId:$sectionId",
                        ExistingWorkPolicy.REPLACE,
                        imgWorkerList
                    )
                    processCounter[sectionId] = Counter(totalImageCount)
                    beginWith.workInfosLiveData.observeForever {
                            itList ->
                        val finishCount = itList.count { it.state.isFinished }

                        processCounter[sectionId]?.setProcess(finishCount)
                        Log.d("work", "section $sectionId finish $finishCount")
                        if (finishCount < totalImageCount) {
                            refreshListener?.doRefreshProcess(
                                sectionId,
                                0,
                                finishCount,
                                totalImageCount
                            )
                        } else {
                            processCounter.remove(sectionId)
                            viewWork()
                        }
                    }

                    val downloadCompleteWorker = OneTimeWorkRequestBuilder<DownloadCompleteWorker>()
                        .addTag("sectionComplete")
                        .addTag("sectionId:${sectionId}")
                        .addTag("complete:${sectionId}")
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(
                            workDataOf(
                                "sectionId" to sectionId,
                            )
                        )
                        .build()

                    beginWith.then(downloadCompleteWorker).enqueue();

                }
            }
    }

    private fun viewWork() {
        val works = WorkManager.getInstance(this).getWorkInfosByTag("sectionStart").get()
        pendingSectionBeanList = works.filter {
            val sectionId = it.tags.first { tag -> tag.startsWith("sectionId") }.split(":")[1]

            val workQuery = WorkQuery.Builder
                .fromStates(listOf(WorkInfo.State.SUCCEEDED))
                .addTags(listOf("complete:$sectionId"))
                .build()
            val workInfos = WorkManager.getInstance(this).getWorkInfos(workQuery).get()
            workInfos.size == 0
        }.map { it ->
            val sectionId = it.tags.first { tag -> tag.startsWith("sectionId") }.split(":")[1]
            val picSectionBean = picSectionDao.getByInnerIndex(sectionId.toLong())
            val imgWorks =
                WorkManager.getInstance(this).getWorkInfosByTag("sectionId:$sectionId:image").get()
            val finishCount = imgWorks.count { it.state == WorkInfo.State.SUCCEEDED }
            val totalCount = imgWorks.size
            Log.d("main", "process for $sectionId: $finishCount / $totalCount");
            PicSectionData(picSectionBean, totalCount).apply { this.process = finishCount }
        }.toMutableList()
        refreshListener?.notifyListReady()
    }

}


class Counter(val max: Int, ) {
    private val process: AtomicInteger = AtomicInteger(0)

    fun setProcess(value: Int) {
        process.set(value)
    }

    fun getProcess() = process.get()
}
