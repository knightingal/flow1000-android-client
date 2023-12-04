package com.example.jianming.Tasks

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class SectionWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams ) {
    override suspend fun doWork(): Result {
        Log.d("CoroutineWorker", "doWork")
        Thread.sleep(20 * 1000)
        Log.d("CoroutineWorker", "finish")
        return Result.success()
    }
}