package com.example.jianming.services

import SERVER_IP
import SERVER_PORT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.workDataOf
import com.example.jianming.Tasks.BatchDownloadImageWorker
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.Tasks.DownloadCompleteWorker
import com.example.jianming.Tasks.DownloadSectionWorker
import com.example.jianming.util.AppDataBase
import com.example.jianming.beans.PicSectionBean
import com.example.jianming.beans.PicSectionData
import com.example.jianming.beans.UpdateStamp
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.util.TimeUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.nanjing.knightingal.processerlib.RefreshListener
import java.util.UUID
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

class DownloadService : Service() {
    companion object {
        private val processCounter = hashMapOf<Long, Counter>()
        private var refreshListener: RefreshListener? = null
    }

    private val binder: IBinder = LocalBinder();

    private lateinit var db: AppDataBase
    private lateinit var picSectionDao : PicSectionDao
    private lateinit var updateStampDao: UpdataStampDao
    private lateinit var picInfoDao : PicInfoDao



    fun setRefreshListener(refreshListener: RefreshListener?) {
        DownloadService.refreshListener = refreshListener
    }

    fun getProcessCounter():HashMap<Long, Counter> {
        return processCounter
    }

    fun removeRefreshListener() {
        refreshListener = null
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

    private val workerQueue: BlockingQueue<PicSectionBean> = LinkedBlockingQueue()


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
//            workerQueue.put(allPicSectionBeanList[4])
//            workerQueue.put(allPicSectionBeanList[5])
//            workerQueue.put(allPicSectionBeanList[6])
//            workerQueue.put(allPicSectionBeanList[7])
//            workerQueue.put(allPicSectionBeanList[8])
//            workerQueue.put(allPicSectionBeanList[9])
//            workerQueue.poll()?.let { startWork(it.id) }
//            workerQueue.poll()?.let { startWork(it.id) }
//            startWork(4L)
//            startWork(30L)
//            startWork(31L)
//            viewWork()

            if (true) {
                val pendingUrl =
                    "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=PENDING"
                val pendingJob = ConcurrencyJsonApiTask.startDownload(pendingUrl) { pendingBody ->
                    val picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                    workerQueue.addAll(picSectionBeanList)
                    pendingSectionBeanList.addAll(picSectionBeanList.map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } })
                }
                val localUrl =
                    "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=LOCAL"
                val localJob = ConcurrencyJsonApiTask.startDownload(localUrl) { pendingBody ->
                    val picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                    workerQueue.addAll(picSectionBeanList)
                    pendingSectionBeanList.addAll(picSectionBeanList.map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } })
                }
                MainScope().launch {
                    listOf(pendingJob, localJob).joinAll()
                    pendingSectionBeanList.sortBy { it.picSectionBean.id }
                    workerQueue.poll()?.let { startWork(it.id, applicationContext) }
                    workerQueue.poll()?.let { startWork(it.id, applicationContext) }
                    viewWork(applicationContext)
//                    workerQueue.poll()?.let { startWork(it.id) }
//                    workerQueue.poll()?.let { startWork(it.id) }
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

    private fun createBatchDownloadImageWorker(sectionId: Long): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<BatchDownloadImageWorker>()
            .addTag("batchDownloadImage:$sectionId")
            .setInputData(workDataOf("sectionId" to sectionId))
            .build()
    }


    private fun createHeaderWorker(sectionId: Long, context: Context): OneTimeWorkRequest? {

        val workQuery: WorkQuery = WorkQuery.fromUniqueWorkNames("downloadTaskHeader:${sectionId}")
        val existHeaderWorker = WorkManager.getInstance(context).getWorkInfos(workQuery).get()
        if (existHeaderWorker.size > 0) {
            Log.i("DownloadService", "downloadTaskHeader:${sectionId} exist, skip to create worker")
            return null
        }
        return OneTimeWorkRequestBuilder<DownloadSectionWorker>()
            .addTag("sectionStart")
            .addTag("sectionId:${sectionId}")
            .setInputData(
                workDataOf(
                    DownloadSectionWorker.PARAM_SECTION_ID_KEY to sectionId
                )
            ).build()
    }


    private fun createDownloadCompleteWorker(sectionId: Long): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<DownloadCompleteWorker>()
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
    }



    private fun genObserver(sectionId: Long, workerId: UUID): Observer<List<WorkInfo>> {
        val observer = object: Observer<List<WorkInfo>>  {
            override fun onChanged(value: List<WorkInfo>) {
                val batchDownloadImageWorkerStatus = value.find { predicate -> predicate.id == workerId }
                if (batchDownloadImageWorkerStatus?.state == WorkInfo.State.RUNNING ) {
                    val progress = batchDownloadImageWorkerStatus.progress.getInt("progress", 0)
                    val total = batchDownloadImageWorkerStatus.progress.getInt("total", 0)
                    if (processCounter[sectionId] == null && total != 0) {
                        processCounter[sectionId] = Counter(total)
                    }
                    processCounter[sectionId]?.setProcess(progress)
                    refreshListener?.doRefreshProcess(sectionId, 0, progress, total)
                }
                if (batchDownloadImageWorkerStatus?.state == WorkInfo.State.SUCCEEDED) {
                    val total = batchDownloadImageWorkerStatus.outputData.getInt("total", 0)
                    if (processCounter[sectionId] == null && total != 0) {
                        processCounter[sectionId] = Counter(total)
                    }
                    processCounter[sectionId]?.setProcess(total)
                    refreshListener?.doRefreshProcess(sectionId, 0, total, total)
                }

                if (value.none { predicate -> predicate.tags.contains(DownloadCompleteWorker::class.java.name) && predicate.state.isFinished }) {
                    return
                }
                workerQueue.poll()?.let { startWork(it.id, applicationContext) }
                viewWork(applicationContext)
            }
        }
        return observer
    }

    private fun startWork(sectionId: Long, context: Context) {

        val downloadSectionRequest = createHeaderWorker(sectionId, context) ?:
            return
        val batchDownloadImageWorker = createBatchDownloadImageWorker(sectionId)
        val then = WorkManager.getInstance(context).beginUniqueWork(
            "downloadTaskHeader:${sectionId}",
            ExistingWorkPolicy.KEEP,
            downloadSectionRequest
        ).then(batchDownloadImageWorker)
            .then(createDownloadCompleteWorker(sectionId))

        then.workInfosLiveData.observeForever(genObserver(sectionId, batchDownloadImageWorker.id))
        then.enqueue()

//        WorkManager.getInstance(this).getWorkInfoByIdLiveData(downloadSectionRequest.id)
//            .observeForever(genHeaderObserver(sectionId))
    }

    private fun viewWork(context: Context) {
        val works = WorkManager.getInstance(context).getWorkInfosByTag("sectionStart").get()
        works.filter {
            val sectionId = it.tags.first { tag -> tag.startsWith("sectionId") }.split(":")[1]

            val workQuery = WorkQuery.Builder
                .fromStates(listOf(WorkInfo.State.SUCCEEDED))
                .addTags(listOf("complete:$sectionId"))
                .build()
            val workInfos = WorkManager.getInstance(context).getWorkInfos(workQuery).get()
            workInfos.size == 0
        }.forEach { it ->
            val sectionId = it.tags.first { tag -> tag.startsWith("sectionId") }.split(":")[1]
            val imgWorks =
                WorkManager.getInstance(context).getWorkInfosByTag("batchDownloadImage:$sectionId").get()
            var progress = 0
            var total = 0
            if (imgWorks.size != 0) {
                val imgWork = imgWorks[0]
                if (imgWork.state.isFinished) {
                    progress = imgWork.outputData.getInt("total", 0)
                    total = progress
                } else {
                    progress = imgWork.progress.getInt("progress", 0)
                    total = imgWork.progress.getInt("total", 0)
                }
            }
            Log.d("main", "process for $sectionId: $progress / $total");
            if (processCounter[sectionId.toLong()] == null && total != 0) {
                processCounter[sectionId.toLong()] = Counter(total)
            }
            processCounter[sectionId.toLong()]?.setProcess(progress)
//            PicSectionData(picSectionBean, total).apply { this.process = progress }
        }
//        pendingWorkerList.forEach { pending ->
//            val sectionId = pending.picSectionBean.id
//            pendingSectionBeanList.find { bean -> bean.picSectionBean.id == sectionId }.apply {
//                this?.totalCount = pending.totalCount
//                this?.process = pending.process
//            }
//        }
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
