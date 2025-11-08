package org.knightingal.flow1000.client.util

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtil {

    fun formatTitle(sourceTitle: String): String {
        if (sourceTitle.length > 14) {
            val timeStamp = sourceTitle.take(14)
            var isTimeStamp = true
            try {
                SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE).parse(timeStamp)
            } catch (_: ParseException) {
                isTimeStamp = false
            }
            return if (isTimeStamp) sourceTitle.substring(14) else sourceTitle
        }
        return sourceTitle
    }

    @SuppressLint("SimpleDateFormat")
    fun currentTimeFormat(): String {
        val df = SimpleDateFormat("yyyyMMddHHmmss")
        return df.format(Date())
    }
}