package org.knightingal.flow1000.client.task

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ConcurrencyApkTask {
    private const val TAG = "DLImageTask"

    private fun makeClient() : HttpClient = HttpClient(OkHttp) {
            engine {
                addNetworkInterceptor { chain ->
                    val originalResponse: Response = chain.proceed(chain.request())
                    val body = originalResponse.body
                    val wrappedBody = ResponseBodyListener(body!!)
                    originalResponse.newBuilder().body(wrappedBody).build()
                }
            }
        }

    suspend fun downloadToFile(src: String, dest: File) {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "start download $src")
            val client = makeClient()
            while (true) {
                try {
                    val bytes: ByteArray = client.get(src).body()
                    val fileOutputStream = FileOutputStream(dest, false)
                    fileOutputStream.write(bytes)
                    fileOutputStream.close()
                    break
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e(TAG, "download $src error")
                }
            }
        }

    }
}