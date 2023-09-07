package com.example.jianming.Utils

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.OkHttpClient

object NetworkUtil {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val currentNetWork = connectivityManager.activeNetwork
        return currentNetWork != null
    }

    val okHttpClient = OkHttpClient()
}