package com.example.jianming.Tasks

import android.content.Context
import androidx.room.Room
import com.example.jianming.Utils.AppDataBase
import com.example.jianming.Utils.NetworkUtil
import com.example.jianming.Utils.TimeUtil
import com.example.jianming.beans.PicAlbumBean
import com.example.jianming.dao.PicAlbumDao
import com.example.jianming.dao.UpdataStampDao
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.lang.Exception

class ConcurrencyDownloadAlbumsTask {

    var picAlbumDao: PicAlbumDao

    var updateStampDao: UpdataStampDao

    var db: AppDataBase

    var context: Context;

    constructor(context: Context) {
        this.context = context
        var db = Room.databaseBuilder(context, AppDataBase::class.java, "database-flow1000")
            .allowMainThreadQueries().build()
        this.picAlbumDao = db.picAlbumDao()
        this.updateStampDao = db.updataStampDao()
        this.db = db
    }

    fun startDownload(url: String, callBack: (body: String) -> Unit): Unit {
        runBlocking {
            launch {
                val body = makeRequest(url)
                callBack(body)
            }
        }
    }

    private suspend fun makeRequest(url: String): String {
        return withContext(Dispatchers.IO) {
            var request = Request.Builder().url(url).build()

            var body = NetworkUtil.getOkHttpClient().newCall(request).execute().body.string()

            body
        }
    }

}