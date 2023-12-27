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
        var refreshListener: MutableSet<RefreshListener> = mutableSetOf()
        var pendingSectionBeanList: MutableList<PicSectionData> = mutableListOf()
        val workerQueue: BlockingQueue<PicSectionBean> = LinkedBlockingQueue()
        val existSectionId: MutableSet<Long> = mutableSetOf()
    }

    private val binder: IBinder = LocalBinder();

    private lateinit var db: AppDataBase
    private lateinit var picSectionDao : PicSectionDao
    private lateinit var updateStampDao: UpdataStampDao
    private lateinit var picInfoDao : PicInfoDao



    fun setRefreshListener(refreshListener: RefreshListener?) {
        if (refreshListener != null) {
            DownloadService.refreshListener.add(refreshListener)
        }
    }

    fun getProcessCounter():HashMap<Long, Counter> {
        return TaskManager.processCounter
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

    private fun checkSectionWorkerExist(sectionId: Long): Boolean {
        if (existSectionId.contains(sectionId)) {
            return true
        }
        val picSectionBean = picSectionDao.getByServerIndex(sectionId)
        return picSectionBean.exist == 1
    }

    fun fetchAllSectionList() {
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
                .map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } }
            refreshListener.forEach {
                it.notifyListReady()
            }
        }

    }

    fun startDownloadSectionList() {
        val existPending = picSectionDao.getByClientStatus(PicSectionBean.ClientStatus.PENDING)
        pendingSectionBeanList.addAll(
            existPending.map { bean -> PicSectionData(bean, 0) }
        )
        workerQueue.addAll(existPending)
        existSectionId.addAll(existPending.map { it.id })

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

            allPicSectionBeanList = picSectionDao.getAll().toList().map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } }

            val pendingUrl =
                "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=PENDING"
            val pendingJob = ConcurrencyJsonApiTask.startDownload(pendingUrl) { pendingBody ->
                var picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                picSectionBeanList = picSectionBeanList.filter {
                    !checkSectionWorkerExist(it.id)
                }
                existSectionId.addAll(picSectionBeanList.map { it.id })
                workerQueue.addAll(picSectionBeanList)
                pendingSectionBeanList.addAll(picSectionBeanList.map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } })
            }
            val localUrl =
                "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=LOCAL"
            val localJob = ConcurrencyJsonApiTask.startDownload(localUrl) { pendingBody ->
                var picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                picSectionBeanList = picSectionBeanList.filter {
                    !checkSectionWorkerExist(it.id)
                }
                existSectionId.addAll(picSectionBeanList.map { it.id })
                workerQueue.addAll(picSectionBeanList)
                pendingSectionBeanList.addAll(picSectionBeanList.map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } })
            }
            MainScope().launch {
                listOf(pendingJob, localJob).joinAll()
                pendingSectionBeanList.sortBy { it.picSectionBean.id }

                launch {
                    db.runInTransaction() {
                        pendingSectionBeanList.forEach {
                            picSectionDao.updateClientStatusByServerIndex(
                                it.picSectionBean.id,
                                PicSectionBean.ClientStatus.PENDING
                            )
                        }
                    }
                }

                val workQuery = WorkQuery.Builder
                    .fromStates(listOf(WorkInfo.State.RUNNING, WorkInfo.State.BLOCKED, WorkInfo.State.ENQUEUED))
                    .addTags(listOf(
                        "com.example.jianming.Tasks.BatchDownloadImageWorker",
                        "com.example.jianming.Tasks.DownloadSectionWorker",
                    ))
                    .build()

                val workInfoList = WorkManager.getInstance(applicationContext)
                    .getWorkInfos(workQuery).get()
                var i = 0
                while (i < 2-workInfoList.size) {
                    workerQueue.poll()?.let { TaskManager.startWork(it.id, applicationContext) }
                    i++
                }
                TaskManager.viewWork(applicationContext)
            }
            refreshListener.forEach {
                it.notifyListReady()
            }
        }
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


class Counter(val max: Int) {
    private val process: AtomicInteger = AtomicInteger(0)

    fun setProcess(value: Int) {
        process.set(value)
    }

    fun getProcess() = process.get()
}
