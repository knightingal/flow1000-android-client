package com.example.jianming.Tasks

import SERVER_IP
import SERVER_PORT
import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.jianming.Tasks.ConcurrencyJsonApiTask.makeRequest
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.beans.SectionInfoBean
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.util.AppDataBase
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

class DownloadSectionWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams ) {


    private var db: AppDataBase = Room.databaseBuilder(
        applicationContext,
        AppDataBase::class.java, "database-flow1000").allowMainThreadQueries().build()
    private var picSectionDao : PicSectionDao
    private var updataStampDao: UpdataStampDao
    private var picInfoDao : PicInfoDao
    init {
        picSectionDao = db.picSectionDao()
        picInfoDao = db.picInfoDao()
        updataStampDao = db.updateStampDao()
    }
    @OptIn(InternalCoroutinesApi::class)
    override suspend fun doWork(): Result {
        Log.d("DownloadSectionWorker", "doWork")
        val sectionId = inputData.getLong("sectionId", 0)
        val url = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picContentAjax?id=$sectionId"
        val body = makeRequest(url)
        val mapper = jacksonObjectMapper()
        val sectionInfoBean = mapper.readValue<SectionInfoBean>(body)

        val picSectionBean = picSectionDao.getByInnerIndex(sectionId)
        synchronized(AppDataBase::class) {
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
        }



        val picInfoBeanList = picInfoDao.queryBySectionInnerIndex(picSectionBean.id)
        Thread.sleep(10 * 1000)

        return Result.success()
    }
}