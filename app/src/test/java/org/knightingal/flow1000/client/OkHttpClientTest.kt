package org.knightingal.flow1000.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import org.junit.Test

class OkHttpClientTest {

    @Test
    fun clientTest() = runBlocking{
        launch {
            val client = OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    val originResponse = chain.proceed(chain.request())
                    val body = originResponse.body!!
                    val wrappedBody = ResponseBodyListener(body)
                    println("content length ${wrappedBody.contentLength()}")
                    originResponse.newBuilder().body(wrappedBody).build()
                }
                .build()
            val request = Request
                .Builder()
                .url("http://192.168.2.12:3002/apks/org.nanking.flow1000_admin_251018015_251018-b4f5af1.apk")
                .build()
            val body = client.newCall(request).execute().body!!.bytes()
//            println(body)

        }
        Unit

    }

    @Test
    fun clientKtorTest() = runBlocking{
        launch {
            val body: String = HttpClient(OkHttp)
                .get("http://localhost:8082/").body()
            println(body)

        }
        Unit

    }
}

class ByteCounter(val totalBytes: Long) {
    var bytesReadSoFar: Long = 0
    fun update(bytesRead: Long) {
        bytesReadSoFar += bytesRead
        val progress = bytesReadSoFar * 100 / totalBytes
        println("download progress: $progress% ($bytesReadSoFar/$totalBytes)")
    }
}

class ResponseBodyListener(val origin: okhttp3.ResponseBody): okhttp3.ResponseBody() {
    val byteCounter = ByteCounter(contentLength())
    override fun contentLength(): Long {
        val contentLength = origin.contentLength()
        println("content length: $contentLength")

        return contentLength
    }

    override fun contentType(): okhttp3.MediaType? {
        return origin.contentType()
    }

    private var bufferedSource: BufferedSource? = null

    override fun source(): BufferedSource {
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
