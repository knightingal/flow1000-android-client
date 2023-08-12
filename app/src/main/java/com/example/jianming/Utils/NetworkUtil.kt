package com.example.jianming.Utils

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.OkHttpClient

object NetworkUtil {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (connectivityManager != null) {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } else {
            false
        }
    }

    val okHttpClient = OkHttpClient()
}