package com.example.jianming.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object TimeUtil {

    @SuppressLint("SimpleDateFormat")
    fun currentTimeFormat(): String {
        val df = SimpleDateFormat("yyyyMMddHHmmss")
        return df.format(Date())
    }
}