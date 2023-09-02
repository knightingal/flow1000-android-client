package com.example.jianming.Tasks

import android.graphics.BitmapFactory
import android.util.Log
import com.example.jianming.Utils.Decryptor
import com.example.jianming.Utils.NetworkUtil
import com.example.jianming.beans.PicInfoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException

object ConcurrencyImageTask {
    private const val TAG = "DLImageTask"
    fun downloadUrl(src: String, dest: File, encrypted: Boolean, callback: (bytes: ByteArray) -> Unit): Unit {
        MainScope().launch {
            val bytes = makeRequest(src, dest, encrypted)
            if (bytes != null) {
                callback(bytes)
            }
        }
    }

    private suspend fun makeRequest(src: String, dest: File, encrypted: Boolean): ByteArray? {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "start download $src")
            val request = Request.Builder().url(src).build()
            var bytes: ByteArray?
            while (true) {
                try {
                    bytes = NetworkUtil.okHttpClient.newCall(request).execute().body.bytes()
                    val options: BitmapFactory.Options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    if (encrypted) {
                        bytes = Decryptor.decrypt(bytes)
                    }
                    val fileOutputStream = FileOutputStream(dest, true)
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