package com.example.jianming.Tasks

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DownloadCompleteWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams ) {
    override suspend fun doWork(): Result {
        val sectionId = inputData.getLong("sectionId", 0)

        Log.d("DownloadCompleteWorker", "$sectionId finish")
        return Result.success()
    }


}