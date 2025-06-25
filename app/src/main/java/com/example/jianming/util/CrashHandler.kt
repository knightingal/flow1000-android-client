package com.example.jianming.util

import android.os.Environment
import android.text.TextUtils
import com.example.jianming.myapplication.App
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer

class CrashHandler(private val app: App) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(thread: Thread, e: Throwable) {
        val stackTraceInfo = getStackTraceInfo(e)
    }

    private fun getStackTraceInfo(throwable: Throwable): String {
        var pw: PrintWriter? = null
        val writer: Writer = StringWriter()
        try {
            pw = PrintWriter(writer)
            throwable.printStackTrace(pw)
        } catch (e: Exception) {
            return ""
        } finally {
            pw?.close()
        }
        return writer.toString()
    }


    private fun saveThrowableMessage(errorMessage: String) {
        if (TextUtils.isEmpty(errorMessage)) {
            return
        }

        val file = File(
            app.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS
            ), "crash"
        )
        if (!file.exists()) {
            val mkdirs = file.mkdirs()
            if (mkdirs) {
                writeStringToFile(errorMessage, file)
            }
        } else {
            writeStringToFile(errorMessage, file)
        }
    }

    private fun writeStringToFile(errorMessage: String, file: File) {
        Thread {
            var outputStream: FileOutputStream? = null
            try {
                val inputStream = ByteArrayInputStream(errorMessage.toByteArray())
                outputStream = FileOutputStream(
                    File(
                        file,
                        System.currentTimeMillis().toString() + ".txt"
                    )
                )
                var len = 0
                val bytes = ByteArray(1024)
                while ((inputStream.read(bytes).also { len = it }) != -1) {
                    outputStream.write(bytes, 0, len)
                }
                outputStream.flush()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }
}
