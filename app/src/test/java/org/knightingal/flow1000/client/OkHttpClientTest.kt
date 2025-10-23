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
import okhttp3.internal.wait
import org.junit.Assert.assertTrue
import org.junit.Test

class OkHttpClientTest {

    @Test
    fun clientTest() = runBlocking{
        launch {
            val client = OkHttpClient()
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