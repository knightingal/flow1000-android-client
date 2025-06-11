package com.example.jianming.Tasks

import android.graphics.BitmapFactory
import android.util.Log
import com.example.jianming.util.Decryptor
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
            val client = HttpClient(CIO)
            var bytes: ByteArray?
            while (true) {
                try {
                    bytes = client.get(src).body()
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

    public suspend fun makeRequest(src: String, encrypted: Boolean): ByteArray? {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "start download $src")
            val client = HttpClient(CIO)
            var bytes: ByteArray?
            while (true) {
                try {
                    bytes = client.get(src).body()
                    val options: BitmapFactory.Options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    if (encrypted) {
                        bytes = Decryptor.decrypt(bytes)
                    }
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