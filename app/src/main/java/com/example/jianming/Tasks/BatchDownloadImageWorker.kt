package com.example.jianming.Tasks

import SERVER_IP
import SERVER_PORT
import android.content.Context
import android.graphics.BitmapFactory
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
import com.example.jianming.myapplication.App
import com.example.jianming.myapplication.getSectionConfig
import com.example.jianming.services.Counter
import com.example.jianming.util.AppDataBase
import com.example.jianming.util.FileUtil.getSectionStorageDir
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicInteger

class BatchDownloadImageWorker(context: Context, workerParams: WorkerParameters) :
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
        val picSectionBean = picSectionDao.getByInnerIndex(sectionId)
        val sectionConfig = getSectionConfig(picSectionBean.album)
//        val picName = inputData.getString("picName") as String
        val dirName = inputData.getString("dirName") as String

        val jobList = mutableListOf<Job>()
        val picInfoBeanList = picInfoDao.queryBySectionInnerIndex(sectionId)
        setProgress(workDataOf("progress" to 0, "total" to picInfoBeanList.size))
        Log.d("BatchDownloadImageWorker", "start to download section: $sectionId")
        val progress = AtomicInteger(0)
        picInfoBeanList.forEach { picInfoBean ->
            val picName = picInfoBean.name
            val imgUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                    "/linux1000/${sectionConfig.baseUrl}/${dirName}/${if (sectionConfig.encryped) "$picName.bin" else picName}"

            val job = MainScope().launch {
                Log.d("BatchDownloadImageWorker", "start to download $imgUrl")
                val body = makeRequest(imgUrl, sectionConfig.encryped) as ByteArray

                val directory =
                    getSectionStorageDir(applicationContext, dirName)
                val dest = File(directory, picName)
                val fileOutputStream = FileOutputStream(dest, true)
                fileOutputStream.write(body)
                fileOutputStream.close()
                val currentProgress = progress.incrementAndGet()
                setProgress(workDataOf("progress" to currentProgress, "total" to picInfoBeanList.size))
                Log.d("BatchDownloadImageWorker", "finish download $imgUrl")
            }
            jobList.add(job)
        }
        jobList.joinAll()

//        setProgress(workDataOf("progress" to picInfoBeanList.size, "total" to picInfoBeanList.size))
        Log.d("BatchDownloadImageWorker", "finish download section: $sectionId")
        return Result.success(workDataOf("total" to picInfoBeanList.size))
//        val imgUrl = inputData.getString("imgUrl") as String
//        val picId = inputData.getLong("picId", 0)
//        Log.d("DownloadImageWorker", "start work for:$imgUrl")
//        val body = makeRequest(imgUrl, encrypted) as ByteArray
//        val output: Data = workDataOf("imgUrl" to imgUrl,)
//
//        val directory =
//            getSectionStorageDir(applicationContext, dirName)
//        val dest = File(directory, picName)
//        val fileOutputStream = FileOutputStream(dest, true)
//        fileOutputStream.write(body)
//        fileOutputStream.close()
//
//        val options = BitmapFactory.Options()
//        options.inJustDecodeBounds = true
//        BitmapFactory.decodeByteArray(body, 0, body.size, options)
//        val width = options.outWidth
//        val height = options.outHeight
//        val absolutePath = dest.absolutePath
//        val picInfoBean = picInfoDao.query(picId)
//        picInfoBean.absolutePath = absolutePath
//        picInfoBean.width = width
//        picInfoBean.height = height
//        picInfoDao.update(picInfoBean)
//        return Result.success(output)
    }
}