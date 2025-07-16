package com.example.jianming.Tasks

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ConcurrencyApkTask {
    private const val TAG = "DLImageTask"

    suspend fun makeRequest(src: String, dest: File): ByteArray? {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "start download $src")
            val client = HttpClient(CIO)

            var bytes: ByteArray?
            while (true) {
                try {
                    bytes = client.get(src).body()
                    val fileOutputStream = FileOutputStream(dest, false)
                    fileOutputStream.write(bytes)
                    fileOutputStream.close()
                    break
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e(TAG, "download $src error")
                }
            }
            bytes
        }

    }
}