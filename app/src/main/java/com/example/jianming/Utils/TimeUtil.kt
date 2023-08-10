package com.example.jianming.Utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar

object TimeUtil {

    @SuppressLint("SimpleDateFormat")
    fun currentTimeFormat(): String {
        val df = SimpleDateFormat("yyyyMMddHHmmss")
        return df.format(Date())
    }
}