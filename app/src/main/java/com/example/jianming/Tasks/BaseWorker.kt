package com.example.jianming.Tasks;

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.CoroutineWorker

class BaseWorker(context: Context, workerParams: WorkerParameters) :
        CoroutineWorker(context, workerParams ) {
    override suspend fun doWork(): Result {
        val sleepTime = inputData.getLong("sleepTime", 0)
        Thread.sleep(sleepTime * 1000)
        return Result.success()
    }
}
