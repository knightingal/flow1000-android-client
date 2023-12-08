package com.example.jianming.Tasks

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.jianming.Tasks.ConcurrencyImageTask.makeRequest
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.beans.SectionInfoBean
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.services.Counter
import com.example.jianming.util.AppDataBase
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class DownloadImageWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams ) {

    private val picSectionDao : PicSectionDao
    private val picInfoDao : PicInfoDao
    private val db: AppDataBase = Room.databaseBuilder(
        applicationContext,
        AppDataBase::class.java, "database-flow1000"
    ).allowMainThreadQueries().build()

    init {
        picSectionDao = db.picSectionDao()
        picInfoDao = db.picInfoDao()
    }
    override suspend fun doWork(): Result {
        val imgUrl = inputData.getString("imgUrl") as String
        val encrypted = inputData.getBoolean("encrypted", false)
        Log.d("DownloadImageWorker", "start work for:$imgUrl")
        val body = makeRequest(imgUrl, encrypted)
//        val output: Data = workDataOf("imgContent" to body)

//        val picSectionBean = picSectionDao.getByInnerIndex(sectionIndex)
//        val mapper = jacksonObjectMapper()
//        db.runInTransaction() {
//            (mapper.readValue(body) as SectionInfoBean).pics.forEach { pic ->
//                val picInfoBean: PicInfoBean = PicInfoBean(
//                    null,
//                    pic,
//                    picSectionBean.id,
//                    null,
//                    0,
//                    0
//                )
//                picInfoDao.insert(picInfoBean)
//            }
//        }
//        Log.d("DownloadImageWorker", "work for:$imgUrl finish")
//
//        val picInfoBeanList = picInfoDao.queryBySectionInnerIndex(picSectionBean.id)


        return Result.success()
    }
}