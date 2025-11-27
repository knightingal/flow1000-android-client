package org.knightingal.flow1000.client.task

import android.util.Log
//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.engine.cio.CIO
//import io.ktor.client.engine.okhttp.OkHttp
//import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.knightingal.flow1000.client.myapplication.AboutActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ConcurrencyApkTask {
    private const val TAG = "DLImageTask"

//    private fun makeClient(listener: AboutActivity.DownloadCounterListener) : HttpClient = HttpClient(OkHttp) {
//            engine {
//                addNetworkInterceptor { chain ->
//                    val originalResponse: Response = chain.proceed(chain.request())
//                    val body = originalResponse.body
//                    val wrappedBody = ResponseBodyListener(body!!, listener)
//                    originalResponse.newBuilder().body(wrappedBody).build()
//                }
//            }
//        }

    private fun makeClient(listener: AboutActivity.DownloadCounterListener): OkHttpClient {
        return OkHttpClient.Builder().addNetworkInterceptor { chain ->
            val originalResponse: Response = chain.proceed(chain.request())
            val body = originalResponse.body
            val wrappedBody = ResponseBodyListener(body, listener)
            originalResponse.newBuilder().body(wrappedBody).build()
        }.build()
    }

    suspend fun downloadToFile(src: String, dest: File, listener: AboutActivity.DownloadCounterListener) {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "start download $src")
            val client = makeClient(listener)
            val request = Request.Builder().url(src).build()
            val bytes: ByteArray = client.newCall(request).execute().body.bytes()
            val fileOutputStream = FileOutputStream(dest, false)
            fileOutputStream.write(bytes)
            fileOutputStream.close()
        }

    }
}