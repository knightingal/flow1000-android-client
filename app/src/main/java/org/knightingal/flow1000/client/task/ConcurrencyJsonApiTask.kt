package org.knightingal.flow1000.client.task

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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