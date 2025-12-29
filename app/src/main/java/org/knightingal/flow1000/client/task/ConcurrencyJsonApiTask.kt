package org.knightingal.flow1000.client.task

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import org.knightingal.flow1000.client.myapplication.AboutActivity

//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody

object ConcurrencyJsonApiTask {

    fun startGet(url: String, callBack: (body: String) -> Unit): Job {
        return MainScope().launch {
            callBack(makeRequest(url))
        }
    }

    fun startPost(url: String, body: String, callBack: (body: String) -> Unit) {
        MainScope().launch {
            callBack(makePost(url, body))
        }
    }

    private suspend fun makePost(url: String, body: String): String {
        return withContext(Dispatchers.IO) {
            ""
//            val requestBody = body.toRequestBody("application/json; charset=utf-8".toMediaType())
//            val request = Request.Builder().url(url).method("POST", requestBody).build()
//            NetworkUtil.okHttpClient.newCall(request).execute().body.string()
        }
    }

    suspend fun makeRequest(url: String): String {
        return withContext(Dispatchers.IO) {
            val client = HttpClient(CIO)
            val response: HttpResponse = client.get(url)
            response.body()
        }
    }

}

class ByteCounter(val totalBytes: Long, val listener: AboutActivity.DownloadCounterListener) {
    var bytesReadSoFar: Long = 0
    fun update(bytesRead: Long) {
        bytesReadSoFar += bytesRead
        val progress = bytesReadSoFar * 100 / totalBytes
        println("download progress: $progress% ($bytesReadSoFar/$totalBytes)")
        listener.update(bytesReadSoFar, totalBytes)
    }
}

class ResponseBodyListener(val origin: okhttp3.ResponseBody, listener: AboutActivity.DownloadCounterListener): okhttp3.ResponseBody() {
    val byteCounter = ByteCounter(contentLength(), listener)

    override fun contentLength(): Long {
        return origin.contentLength() ?: 0
    }

    private var bufferedSource: BufferedSource? = null

    override fun contentType(): okhttp3.MediaType? {
        return origin.contentType()
    }

    override fun source(): okio.BufferedSource {
        if (bufferedSource ==null) {
            bufferedSource = source(origin.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: okio.Source): okio.Source {
        return object : ForwardingSource(source) {
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                println("bytesRead: $bytesRead")
                if (bytesRead >= 0) {
                    byteCounter.update(bytesRead)
                }
                return bytesRead
            }
        }
    }

}

