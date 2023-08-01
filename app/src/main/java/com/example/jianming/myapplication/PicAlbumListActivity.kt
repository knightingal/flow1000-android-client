package com.example.jianming.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room.databaseBuilder
import com.example.jianming.Tasks.ConcurrencyImageTask
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.Tasks.DLAlbumTask
import com.example.jianming.Utils.AppDataBase
import com.example.jianming.Utils.EnvArgs
import com.example.jianming.Utils.NetworkUtil
import com.example.jianming.Utils.TimeUtil
import com.example.jianming.beans.AlbumInfoBean
import com.example.jianming.beans.PicAlbumBean
import com.example.jianming.beans.PicAlbumData
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.dao.PicAlbumDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.listAdapters.PicAlbumListAdapter
import com.example.jianming.services.KtDownloadService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.nanjing.knightingal.processerlib.RefreshListener
import org.nanjing.knightingal.processerlib.beans.CounterBean
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class PicAlbumListActivity : AppCompatActivity(), RefreshListener {


    private val TAG = "PicAlbumListActivityMD"
    private lateinit var db: AppDataBase

    private lateinit var picAlbumDao: PicAlbumDao

    private lateinit var picInfoDao: PicInfoDao

    private lateinit var updataStampDao: UpdataStampDao


    private lateinit var picAlbumListAdapter: PicAlbumListAdapter

    private lateinit var listView: RecyclerView

    private var picAlbumDataList: MutableList<PicAlbumData> = mutableListOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val offline = intent.getBooleanExtra("offline", false)
        isNotExistItemShown = !offline

        db = databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()

        picAlbumDao = db.picAlbumDao()
        picInfoDao = db.picInfoDao()
        updataStampDao = db.updataStampDao()

        setContentView(R.layout.activity_pic_album_list_activity_md)


        listView = findViewById(R.id.list_view11)
        listView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        listView.layoutManager = mLayoutManager

        picAlbumListAdapter = PicAlbumListAdapter(this)
        picAlbumListAdapter.setDataArray(picAlbumDataList)
        listView.adapter = picAlbumListAdapter


    }

    val TYPE_LIST: List<String> = listOf(TAG)

    var downLoadService: KtDownloadService? = null


    var isBound = false
    private val conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Log.d(TAG, "onServiceConnected")
            isBound = true
            downLoadService = (binder as KtDownloadService.LocalBinder).getService() as KtDownloadService
            downLoadService!!.setRefreshListener(
                this@PicAlbumListActivity
            )
            if (isNotExistItemShown && NetworkUtil.isNetworkAvailable(this@PicAlbumListActivity)) {
                startDownloadWebPage()
            } else {
                refreshFrontPage.invoke()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")
            downLoadService = null
            isBound = false
        }
    }

    override fun onPause() {
        super.onPause()
        downLoadService?.removeRefreshListener()
        downLoadService = null
        unbindService(conn)
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, KtDownloadService::class.java), conn, BIND_AUTO_CREATE)
    }

    private fun startDownloadWebPage() {
        val updateStamp = updataStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN")
        val stringUrl = "http://${EnvArgs.serverIP}:${EnvArgs.serverPort}/local1000/picIndexAjax?time_stamp=${updateStamp.updateStamp}"
        Log.d("startDownloadWebPage", stringUrl)
        ConcurrencyJsonApiTask.startDownload(stringUrl, downloadCallback)
    }

    private val downloadCallback: (body: String) -> Unit = {body ->
        val mapper = jacksonObjectMapper()
        try {
            db.beginTransaction()
            val updateStamp = updataStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN")
            updateStamp.updateStamp = TimeUtil.currentFormatyyyyMMddHHmmss()
            updataStampDao.update(updateStamp)
            val picAlbumBeanList: List<PicAlbumBean> = mapper.readValue(body)
            picAlbumBeanList.forEach { picAlbumDao.insert(it) }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }

        refreshFrontPage.invoke()

        val stringUrl = "http://${EnvArgs.serverIP}:${EnvArgs.serverPort}/local1000/picIndexAjax?client_status=PENDING"
        ConcurrencyJsonApiTask.startDownload(stringUrl) { it ->
            val picAlbumBeanList: List<PicAlbumBean> = mapper.readValue(it)
            picAlbumBeanList.forEach{picAlbumDao.update(it)}
        }

    }

    private val refreshFrontPage: () -> Unit = {
        picAlbumDataList.clear()
        val picAlbumBeanList = getDataSourceFromJsonFile()
        for (picAlbumBean in picAlbumBeanList) {
            val picAlbumData = PicAlbumData(picAlbumBean)
            picAlbumDataList.add(picAlbumData)
        }
        picAlbumListAdapter.notifyDataSetChanged()

    }

    private var isNotExistItemShown = true

    private fun getDataSourceFromJsonFile(): List<PicAlbumBean> {
        return if (isNotExistItemShown && NetworkUtil.isNetworkAvailable(this)) {
            picAlbumDao.getAll().toList()
        } else {
            picAlbumDao.getAllExist().toList()
        }
    }


    override fun doRefreshView(position: Int, currCount: Int, max: Int) {
        val viewHolder =
            listView.findViewHolderForAdapterPosition(position) as PicAlbumListAdapter.ViewHolder?
        if (currCount == max) {
//            downLoadService!!.processingIndex.remove(Integer.valueOf(position))
            picAlbumDataList[position].picAlbumData.exist = 1
            picAlbumDao.update(picAlbumDataList[position].picAlbumData)
            picAlbumListAdapter.notifyDataSetChanged()
        }
        if (viewHolder != null) {
            MainScope().launch {
                viewHolder.downloadProcessBar.setProgressCompat(currCount, false)
                viewHolder.downloadProcessBar.max = max
                Log.d(TAG, "current = $currCount max = $max")
            }
        }
    }


    private class RefreshHandler internal constructor(var picAlbumListActivity: PicAlbumListActivity) :
        Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val counterBean = msg.data.getSerializable("data") as CounterBean
            picAlbumListActivity.refreshListItem(counterBean)
        }
    }

    var refreshHandler: Handler = RefreshHandler(this)


    private fun refreshListItem(counterBean: CounterBean) {
        val viewHolder =
            listView.findViewHolderForAdapterPosition(counterBean.index) as PicAlbumListAdapter.ViewHolder?
        if (counterBean.curr == counterBean.max) {
//            downLoadService!!.processingIndex.remove(Integer.valueOf(counterBean.index))
            picAlbumDataList[counterBean.index].picAlbumData.exist = 1
            picAlbumDao.update(picAlbumDataList[counterBean.index].picAlbumData)
            picAlbumListAdapter.notifyDataSetChanged()
        }
        if (viewHolder != null) {
//            viewHolder.downloadProcessBar.percent = counterBean.curr * 100 / counterBean.max
            viewHolder.downloadProcessBar.setProgressCompat(counterBean.curr, false)
            viewHolder.downloadProcessBar.max = counterBean.max
            viewHolder.downloadProcessBar.postInvalidate()
        }
        Log.d(TAG, "current = " + counterBean.curr + " max = " + counterBean.max)
    }


    fun asyncStartDownload(index: Long, position: Int) {
        downLoadService?.startDownloadAlbum(index, position)
    }

}