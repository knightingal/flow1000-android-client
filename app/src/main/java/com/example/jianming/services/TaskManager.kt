package com.example.jianming.services

import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.workDataOf
import com.example.jianming.Tasks.BatchDownloadImageWorker
import com.example.jianming.Tasks.DownloadCompleteWorker
import com.example.jianming.Tasks.DownloadSectionWorker
import java.util.UUID

class TaskManager {
    companion object {
        val processCounter = hashMapOf<Long, Counter>()

        lateinit var applicationContext: Context

        private fun createBatchDownloadImageWorker(sectionId: Long): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<BatchDownloadImageWorker>()
                .addTag("batchDownloadImage:$sectionId")
                .setInputData(workDataOf("sectionId" to sectionId))
                .build()
        }

        private fun createHeaderWorker(sectionId: Long): OneTimeWorkRequest {

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

        private fun haveDownloadCompleteWorkerFinish(value: List<WorkInfo>): Boolean {
            return value.any { predicate ->
                predicate.tags.contains(DownloadCompleteWorker::class.java.name)
                        && predicate.state.isFinished
            }
        }

        fun initForObserver(context: Context) {
            var workQuery = WorkQuery.Builder
                .fromStates(listOf(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING,
                    WorkInfo.State.BLOCKED))
                .addTags(listOf(BatchDownloadImageWorker::class.java.name,))
                .build()
            WorkManager.getInstance(context).getWorkInfosLiveData(workQuery).observeForever {workInfoList ->
                workInfoList.forEach { predicate ->
                    val progress: Int
                    val total: Int
                    val sectionId: Long
                    if (!predicate.state.isFinished) {
                        progress = predicate.progress.getInt("progress", 0)
                        total = predicate.progress.getInt("total", 0)
                        sectionId = predicate.progress.getLong("sectionId", 0)
                    } else {
                        total = predicate.progress.getInt("total", 0)
                        progress = total
                        sectionId = predicate.progress.getLong("sectionId", 0)
                    }
                    if (processCounter[sectionId] == null && total != 0) {
                        processCounter[sectionId] = Counter(total)
                    }
                    processCounter[sectionId]?.setProcess(progress)
                    DownloadService.refreshListener.forEach { it.doRefreshProcess(sectionId, 0, progress, total) }
                }
            }



            workQuery = WorkQuery.Builder
                .fromStates(listOf(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING, WorkInfo.State.BLOCKED
                    ))
                .addTags(listOf(DownloadCompleteWorker::class.java.name,))
                .build()

            WorkManager.getInstance(context).getWorkInfos(workQuery).get().forEach { workInfo ->
                WorkManager.getInstance(context).getWorkInfoByIdLiveData(workInfo.id).observeForever {
                    if (it.state.isFinished) {
                        Log.d("TaskManager", "task finished")
                    }
                }

            }

        }

        private fun genObserver(sectionId: Long, workerId: UUID): Observer<List<WorkInfo>> = Observer { value ->
            val batchDownloadImageWorkerStatus = value.find { predicate -> predicate.id == workerId }
            if (batchDownloadImageWorkerStatus?.state == WorkInfo.State.RUNNING ) {
                val progress = batchDownloadImageWorkerStatus.progress.getInt("progress", 0)
                val total = batchDownloadImageWorkerStatus.progress.getInt("total", 0)
                if (processCounter[sectionId] == null && total != 0) {
                    processCounter[sectionId] = Counter(total)
                }
                processCounter[sectionId]?.setProcess(progress)
                DownloadService.refreshListener.forEach { it.doRefreshProcess(sectionId, 0, progress, total) }
            }
            if (batchDownloadImageWorkerStatus?.state == WorkInfo.State.SUCCEEDED) {
                val total = batchDownloadImageWorkerStatus.outputData.getInt("total", 0)
                if (processCounter[sectionId] == null && total != 0) {
                    processCounter[sectionId] = Counter(total)
                }
                processCounter[sectionId]?.setProcess(total)
                DownloadService.refreshListener.forEach {it.doRefreshProcess(sectionId, 0, total, total)}
            }

            if (haveDownloadCompleteWorkerFinish(value)) {
                DownloadService.workerQueue.poll()?.let { startWork(it.id, applicationContext) }
                viewWork(applicationContext)
            }
        }

        fun viewWork(context: Context) {
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
            }
            DownloadService.refreshListener.forEach{it.notifyListReady()}
        }


        fun startWork(sectionId: Long, context: Context) {
            val downloadSectionRequest = createHeaderWorker(sectionId)
            val batchDownloadImageWorker = createBatchDownloadImageWorker(sectionId)
            val then = WorkManager.getInstance(context).beginUniqueWork(
                "downloadTaskHeader:${sectionId}",
                ExistingWorkPolicy.KEEP,
                downloadSectionRequest
            ).then(batchDownloadImageWorker)
                .then(createDownloadCompleteWorker(sectionId))
//            then.workInfosLiveData.observeForever(genObserver(sectionId, batchDownloadImageWorker.id))
            then.enqueue()
        }

    }
}