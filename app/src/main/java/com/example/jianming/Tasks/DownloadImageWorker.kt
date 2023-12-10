package com.example.jianming.Tasks

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
import com.example.jianming.services.Counter
import com.example.jianming.util.AppDataBase
import com.example.jianming.util.FileUtil.getSectionStorageDir
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.io.FileOutputStream

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
        val picName = inputData.getString("picName") as String
        val dirName = inputData.getString("dirName") as String
        val encrypted = inputData.getBoolean("encrypted", false)
        Log.d("DownloadImageWorker", "start work for:$imgUrl")
        val body = makeRequest(imgUrl, encrypted) as ByteArray
        val output: Data = workDataOf("imgUrl" to imgUrl,)

        val directory =
            getSectionStorageDir(applicationContext, dirName)
        val dest = File(directory, picName)
        val fileOutputStream = FileOutputStream(dest, true)
        fileOutputStream.write(body)
        fileOutputStream.close()

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(body, 0, body.size, options)
        val width = options.outWidth
        val height = options.outHeight
        val absolutePath = dest.absolutePath

//        Thread.sleep(index * 1000L)
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


        return Result.success(output)
    }
}