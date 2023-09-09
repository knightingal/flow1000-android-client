package com.example.jianming.Tasks

import com.example.jianming.util.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request

object ConcurrencyJsonApiTask {

    fun startDownload(url: String, callBack: (body: String) -> Unit): Unit {
        MainScope().launch {
            val body = makeRequest(url)
            callBack(body)
        }
    }

    private suspend fun makeRequest(url: String): String {
        return withContext(Dispatchers.IO) {
            var request = Request.Builder().url(url).build()

            var body = NetworkUtil.okHttpClient.newCall(request).execute().body.string()

            body
        }
    }

}