package com.example.jianming.services

import SERVER_IP
import SERVER_PORT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Environment
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
import com.example.jianming.Tasks.ConcurrencyImageTask
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.Tasks.DownloadCompleteWorker
import com.example.jianming.Tasks.DownloadImageWorker
import com.example.jianming.Tasks.DownloadSectionWorker
import com.example.jianming.util.AppDataBase
import com.example.jianming.beans.SectionInfoBean
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.beans.PicSectionBean
import com.example.jianming.beans.UpdateStamp
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.myapplication.getSectionConfig
import com.example.jianming.util.TimeUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.nanjing.knightingal.processerlib.RefreshListener
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicInteger

class DownloadService : Service() {

    private val binder: IBinder = LocalBinder();

    private lateinit var db: AppDataBase
    private lateinit var picSectionDao : PicSectionDao
    private lateinit var updataStampDao: UpdataStampDao
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
        updataStampDao = db.updateStampDao()
    }
    private lateinit var allPicSectionBeanList:List<PicSectionBean>
    public fun startDownloadSectionList() {
        val updateStamp = updataStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN") as UpdateStamp
        val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?time_stamp=${updateStamp.updateStamp}"
        Log.d("startDownloadWebPage", stringUrl)
        ConcurrencyJsonApiTask.startDownload(stringUrl) { allBody ->
            val mapper = jacksonObjectMapper()
            db.runInTransaction() {
                updateStamp.updateStamp = TimeUtil.currentTimeFormat()
                val picSectionBeanList: List<PicSectionBean> = mapper.readValue(allBody)
                updataStampDao.update(updateStamp)
                picSectionBeanList.forEach { picSectionDao.insert(it) }
            }

            allPicSectionBeanList = picSectionDao.getAll().toList()
            startWork(3L)
            startWork(4L)
            startWork(30L)
            startWork(31L)
            viewWork()
//            refreshListener?.doRefreshList(allPicSectionBeanList)

            if (false) {
                val pendingUrl =
                    "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=PENDING"
                ConcurrencyJsonApiTask.startDownload(pendingUrl) { pendingBody ->
                    val picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                    if (picSectionBeanList.isNotEmpty()) {
                        picSectionBeanList.forEach {
                            picSectionDao.update(it)
                            val allIdList =
                                allPicSectionBeanList.map { sectionBean -> sectionBean.id }.toList()
                            startDownloadSection(it.id, allIdList.indexOf(it.id));
                        }
                    }
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
                                val allIdList =
                                    allPicSectionBeanList.map { sectionBean -> sectionBean.id }
                                        .toList()
                                startDownloadSection(it.id, allIdList.indexOf(it.id));
                            }
                        }
                    }
                }
            }
            refreshListener?.notifyListReady()
        }

    }

    private var pendingSectionBeanList: MutableList<PicSectionBean> = mutableListOf()

    fun doDownloadImage(url: String, destFile: File) {
        val downloadImageRequest = OneTimeWorkRequestBuilder<DownloadImageWorker>()
            .setInputData(workDataOf(
                "imgUrl" to url,
            )).build()
        WorkManager.getInstance(this).enqueue(downloadImageRequest)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(downloadImageRequest.id).observeForever {into ->
            if (into != null && into.state.isFinished) {
                val imgContent = into.outputData.getByteArray("imgContent")
                val fileOutputStream = FileOutputStream(destFile, true)
                fileOutputStream.write(imgContent)
                fileOutputStream.close()
            }
        }
    }

    fun doDownloadSection(sectionId: Long) {
        val picSectionBean = picSectionDao.getByInnerIndex(sectionId)

        val sectionConfig = getSectionConfig(picSectionBean.album)

        val downloadSectionRequest = OneTimeWorkRequestBuilder<DownloadSectionWorker>()
            .setInputData(workDataOf(
                "sectionId" to sectionId
            )).build()
        WorkManager.getInstance(this).enqueue(downloadSectionRequest)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(downloadSectionRequest.id)
            .observeForever() { workInfo ->
                    if (workInfo != null && workInfo.state.isFinished) {
                        val totalImageCount = workInfo.outputData.getInt(DownloadSectionWorker.TOTAL_IMAGE_COUNT_KEY, 0)
                        Log.d("DownloadService", "worker for $sectionId finish, totalCount: $totalImageCount")
                        val pics = workInfo.outputData.getStringArray("pics") as Array<String>
                        val dirName = workInfo.outputData.getString("dirName") as String
//                        Log.d("pics", pics.toString())

                        val imgWorkerList = pics.map { pic ->

                            val imgUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                                    "/linux1000/${sectionConfig.baseUrl}/${dirName}/${if (sectionConfig.encryped) "$pic.bin" else pic}"

                            OneTimeWorkRequestBuilder<DownloadImageWorker>()
                                .addTag(imgUrl)
                                .setInputData(workDataOf("imgUrl" to imgUrl))
                                .build()
                        }

                        val beginWith = WorkManager.getInstance(this).beginWith(imgWorkerList)
                        beginWith.workInfosLiveData.observeForever {
                            itList ->
                            itList.filter {it -> it.state.isFinished} .forEach { it ->
                                Log.d("DownloadService", "${it.tags.first()} finished") }
                        }
                        beginWith.enqueue()

                    }
            }
    }

    fun startDownloadSection(index: Long, position: Int) {
        pendingSectionBeanList.add(allPicSectionBeanList[position])
        refreshListener?.notifyListReady()

        if (false) {
        doDownloadSection(index)

        val url = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picContentAjax?id=$index"
//        startDownloadSectionWorker(index, url)

            ConcurrencyJsonApiTask.startDownload(url) { body ->
                val picSectionBean = picSectionDao.getByInnerIndex(index)
                val mapper = jacksonObjectMapper()
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

                val picInfoBeanList = picInfoDao.queryBySectionInnerIndex(picSectionBean.id)
                processCounter[index] = Counter(picInfoBeanList.size)

                val sectionInfoBean = SectionInfoBean(
                    "${picSectionBean.id}",
                    picSectionBean.name,
                    mutableListOf()
                )
                picInfoBeanList.forEach { picInfoBean ->
                    sectionInfoBean.pics.add(picInfoBean.name)
                    val picName = picInfoBean.name
                    val sectionConfig = getSectionConfig(picSectionBean.album)

                    var imgUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                            "/linux1000/${sectionConfig.baseUrl}/${sectionInfoBean.dirName}/${picName}"

                    if (sectionConfig.encryped) {
                        imgUrl += ".bin"
                    }

                    val directory =
                        getSectionStorageDir(applicationContext, sectionInfoBean.dirName)
                    val file = File(directory, picName)
                    doDownloadImage(imgUrl, file);

                    ConcurrencyImageTask.downloadUrl(
                        imgUrl,
                        file,
                        sectionConfig.encryped
                    ) { bytes ->

                        val currCount = processCounter[index]?.incProcess() as Int
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                        val width = options.outWidth
                        val height = options.outHeight
                        val absolutePath = file.absolutePath

                        picInfoBean.width = width
                        picInfoBean.height = height
                        picInfoBean.absolutePath = absolutePath

                        if (getRefreshListener() != null) {
                            getRefreshListener()?.doRefreshProcess(
                                picInfoBean.sectionIndex,
                                position,
                                currCount,
                                picInfoBeanList.size
                            )
                        }

                        if (currCount == picInfoBeanList.size) {
                            db.runInTransaction() {
                                for (pic in picInfoBeanList) {
                                    picInfoDao.update(pic)
                                }
                            }
                            allPicSectionBeanList[position].exist = 1
                            picSectionDao.update(allPicSectionBeanList[position])
                            pendingSectionBeanList.remove(allPicSectionBeanList[position])
                            processCounter.remove(index)
                            if (getRefreshListener() != null) {
                                getRefreshListener()?.notifyListReady()
                            }
                            val completeUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                                    "/local1000/completeSection?id=" + index
                            ConcurrencyJsonApiTask.startPost(completeUrl, "") {}
                        }

                    }
                }
            }
        }
    }

    private fun getRefreshListener(): RefreshListener? {
        return refreshListener
    }

    fun getPendingSectionList(): List<PicSectionBean> {
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

        val downloadSectionRequest = OneTimeWorkRequestBuilder<DownloadSectionWorker>()
            .addTag("sectionStart")
            .addTag("sectionId:${sectionId}")
            .setInputData(workDataOf(
                DownloadSectionWorker.PARAM_SECTION_ID_KEY to sectionId
            )).build()
        WorkManager.getInstance(this).enqueue(downloadSectionRequest)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(downloadSectionRequest.id)
            .observeForever() { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    val totalImageCount = workInfo.outputData.getInt(DownloadSectionWorker.TOTAL_IMAGE_COUNT_KEY, 0)
                    Log.d("DownloadService", "worker for $sectionId finish, totalCount=${totalImageCount}")
//                    val pics = workInfo.outputData.getStringArray(PARAM_PICS_KEY) as Array<String>
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
                    beginWith.workInfosLiveData.observeForever {
                            itList ->
                        val finishCount = itList.count { it -> it.state.isFinished }
                        Log.d("work", "section $sectionId finish $finishCount")
                        if (finishCount < totalImageCount) {
                            refreshListener?.doRefreshProcess(
                                sectionId,
                                0,
                                finishCount,
                                totalImageCount
                            )
                        } else {
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
            picSectionBean
        }.toMutableList()
        refreshListener?.notifyListReady()
        works.forEach { it ->
            Log.d("main", "${it.state}");
            val sectionId = it.tags.filter { tag -> tag.startsWith("sectionId") }[0]
            val imgWorks =
                WorkManager.getInstance(this).getWorkInfosByTag("$sectionId:image").get()
            val finishCount = imgWorks.count { it.state == WorkInfo.State.SUCCEEDED }
            val totalCount = imgWorks.size
            Log.d("main", "process for $sectionId: $finishCount / $totalCount");
        }
    }

}

private fun getSectionStorageDir(context: Context, sectionName: String): File {
    val externalFilesDirBase = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val file = File(externalFilesDirBase, sectionName)
    if (file.mkdirs()) {
        Log.i("KtDownloadService", "Directory of $file.absolutePath created")
    }
    return file
}

class Counter(val max: Int, ) {
    private val process: AtomicInteger = AtomicInteger(0)

    fun incProcess(): Int {
        return process.incrementAndGet()
    }

    fun getProcess() = process.get()
}
