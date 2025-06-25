package com.example.jianming.util

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File

/**
 * Created by Jianming on 2015/4/28.
 */
object FileUtil {
    private const val TAG = "FileUtil"

    fun getSectionStorageDir(context: Context, sectionName: String): File {
        val file = File(
            context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS
            ), sectionName
        )
        if (file.mkdirs()) {
            Log.i(TAG, "Directory of " + file.absolutePath + " created")
        }
        return file
    }

    @JvmStatic
    fun removeDir(context: Context, sectionName: String) {
        val file = File(
            context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS
            ), sectionName
        )
        try {
            val files = file.listFiles()
            for (imgFile in files!!) {
                imgFile.delete()
            }
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkDirExist(context: Context, sectionName: String): Boolean {
        val file = File(
            context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS
            ), sectionName
        )

        return file.exists()
    }
}
