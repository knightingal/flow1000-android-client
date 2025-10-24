package org.knightingal.flow1000.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.wait
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
import okio.buffer
import okio.source
import org.junit.Assert.assertTrue
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
                .url("http://localhost:8082")
                .build()
            val body = client.newCall(request).execute().body!!.string()
            println(body)

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

class ResponseBodyListener(val origin: okhttp3.ResponseBody): okhttp3.ResponseBody() {
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
                val bytesread = super.read(sink, byteCount)
                return bytesread
            }
        }
    }

}
