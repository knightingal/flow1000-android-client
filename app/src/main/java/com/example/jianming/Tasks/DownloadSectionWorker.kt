package com.example.jianming.Tasks

import SERVER_IP
import SERVER_PORT
import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.jianming.Tasks.ConcurrencyJsonApiTask.makeRequest
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.beans.SectionInfoBean
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.myapplication.App
import com.example.jianming.myapplication.getSectionConfig
import com.example.jianming.util.AppDataBase
import com.example.jianming.util.FileUtil.getSectionStorageDir
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import java.io.File

class DownloadSectionWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams ) {

    companion object {
        const val PARAM_SECTION_ID_KEY = "sectionId"
        const val PARAM_PICS_KEY = "pics"
        const val PARAM_DIR_NAME_KEY = "dirName"
        const val PARAM_SECTION_BEAN_ID_KEY = "sectionBeanId"
    }


    private var db: AppDataBase = App.findDb()
    private var picSectionDao : PicSectionDao
    private var updataStampDao: UpdataStampDao
    private var picInfoDao : PicInfoDao
    init {
        picSectionDao = db.picSectionDao()
        picInfoDao = db.picInfoDao()
        updataStampDao = db.updateStampDao()
    }
    override suspend fun doWork(): Result {
        Log.d("DownloadSectionWorker", "doWork")
        val sectionId = inputData.getLong(PARAM_SECTION_ID_KEY, 0)
        val url = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picContentAjax?id=$sectionId"
        val body = makeRequest(url)
        val mapper = jacksonObjectMapper()
        val sectionInfoBean = mapper.readValue<SectionInfoBean>(body)

        val picSectionBean = picSectionDao.getByInnerIndex(sectionId)
        db.runInTransaction() {
            sectionInfoBean.pics.forEach { pic ->
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

        val output: Data = workDataOf(
            PARAM_PICS_KEY to sectionInfoBean.pics.toTypedArray(),
            PARAM_SECTION_BEAN_ID_KEY to picSectionBean.id,
            PARAM_DIR_NAME_KEY to sectionInfoBean.dirName,
        )

        if (false) {
            val imgWorkerList = sectionInfoBean.pics.map { pic ->
                val sectionConfig = getSectionConfig(picSectionBean.album)
                val imgUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                        "/linux1000/${sectionConfig.baseUrl}/${sectionInfoBean.dirName}/${if (sectionConfig.encryped) "$pic.bin" else pic}"

                OneTimeWorkRequestBuilder<DownloadImageWorker>()
                    .addTag(imgUrl)
                    .setInputData(workDataOf("imgUrl" to imgUrl))
                    .build()
            }
            WorkManager.getInstance(applicationContext).enqueue(imgWorkerList)
//        imgWorkerList.forEach { imgWorker ->
//            WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(imgWorker.id)
//                .observeForever {workInfo ->
//                    if (workInfo != null && workInfo.state.isFinished) {
//                        Log.d("DownloadSectionWorker", " ${workInfo.tags.first()} downloadImg finished")
//                    }
//                }
//        }

            val picInfoBeanList = picInfoDao.queryBySectionInnerIndex(picSectionBean.id)
        }

        return Result.success(output)
    }
}