package com.example.jianming.Tasks

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.jianming.beans.PicSectionBean
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.myapplication.App
import com.example.jianming.util.AppDataBase

class DownloadCompleteWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams ) {

    private val picSectionDao : PicSectionDao
    private val picInfoDao : PicInfoDao
    private val db: AppDataBase = App.findDb()

    init {
        picSectionDao = db.picSectionDao()
        picInfoDao = db.picInfoDao()
    }
    override suspend fun doWork(): Result {
        val sectionId = inputData.getLong("sectionId", 0)
        val picSectionBean = picSectionDao.getByServerIndex(sectionId)
        picSectionBean.exist = 1
        picSectionBean.clientStatus = PicSectionBean.ClientStatus.LOCAL
        picSectionDao.update(picSectionBean)

        Log.d("DownloadCompleteWorker", "$sectionId finish")
        return Result.success()
    }


}