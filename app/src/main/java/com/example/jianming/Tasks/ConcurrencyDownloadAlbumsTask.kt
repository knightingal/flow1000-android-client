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

    fun startDownload(url: String, callBack: () -> Unit): Unit {
        runBlocking {
            launch {
                makeRequest(url)
                callBack()
            }
        }
    }

    private suspend fun makeRequest(url: String): Unit {
        return withContext(Dispatchers.IO) {
            var request = Request.Builder().url(url).build()

            var body = NetworkUtil.getOkHttpClient().newCall(request).execute().body.string()

            var mapper = jacksonObjectMapper()
            try {
                db.beginTransaction()
                var updateStamp = updateStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN")
                updateStamp.updateStamp = TimeUtil.currentFormatyyyyMMddHHmmss();
                updateStampDao.update(updateStamp)
                var picAlbumBeanList: List<PicAlbumBean> = mapper.readValue(body)
                picAlbumBeanList.forEach { picAlbumDao.insert(it) }
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                db.endTransaction()
            }
        }
    }

}