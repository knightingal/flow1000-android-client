package com.example.jianming.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room.databaseBuilder
import com.example.jianming.Tasks.ConcurrencyDownloadAlbumsTask
import com.example.jianming.Tasks.DownloadPicsTask
import com.example.jianming.Utils.AppDataBase
import com.example.jianming.Utils.EnvArgs
import com.example.jianming.Utils.NetworkUtil
import com.example.jianming.beans.PicAlbumBean
import com.example.jianming.beans.PicAlbumData
import com.example.jianming.dao.PicAlbumDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.listAdapters.PicAlbumListAdapter
import com.example.jianming.services.DownloadService
import org.nanjing.knightingal.processerlib.RefreshListener
import org.nanjing.knightingal.processerlib.Services.DownloadService.LocalBinder
import org.nanjing.knightingal.processerlib.beans.CounterBean

class PicAlbumListActivity : AppCompatActivity(), RefreshListener {


    private val TAG = "PicAlbumListActivityMD"
    private lateinit var db: AppDataBase

    private lateinit var picAlbumDao: PicAlbumDao

    private lateinit var updataStampDao: UpdataStampDao


    private lateinit var picAlbumListAdapter: PicAlbumListAdapter

    private lateinit var listView: RecyclerView

    private var picAlbumDataList: MutableList<PicAlbumData> = mutableListOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()

        picAlbumDao = db.picAlbumDao()
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

    var downLoadService: DownloadService? = null


    var isBound = false
    private val conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected")
            isBound = true
            downLoadService = (service as LocalBinder).service as DownloadService
            downLoadService!!.setRefreshListener(
                TYPE_LIST,
                this@PicAlbumListActivity
            )
            if (NetworkUtil.isNetworkAvailable(this@PicAlbumListActivity)) {
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

    private fun startDownloadWebPage() {
        val (_, _, updateStamp) = updataStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN")
        val stringUrl = String.format(
            "http://%s:%s/local1000/picIndexAjax?time_stamp=%s",
            EnvArgs.serverIP,
            EnvArgs.serverPort,
            updateStamp
        )
        Log.d("startDownloadWebPage", stringUrl)
        ConcurrencyDownloadAlbumsTask(applicationContext).startDownload(stringUrl, refreshFrontPage)
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

    private val isNotExistItemShown = true

    private fun getDataSourceFromJsonFile(): List<PicAlbumBean> {
        return if (isNotExistItemShown && NetworkUtil.isNetworkAvailable(this)) {
            picAlbumDao.getAll().toList()
        } else {
            picAlbumDao.getAllExist().toList()
        }
    }

    override fun onPause() {
        super.onPause()
        downLoadService?.removeListener()
        downLoadService = null
        unbindService(conn)
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, DownloadService::class.java), conn, BIND_AUTO_CREATE)
    }

    override fun doRefreshView(counterBean: CounterBean?) {

        val msg = Message()
        val data = Bundle()
        data.putSerializable("data", counterBean)
        msg.data = data
        refreshHandler.sendMessage(msg)
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
            downLoadService!!.processingIndex.remove(Integer.valueOf(counterBean.index))
            picAlbumDataList[counterBean.index].picAlbumData.exist = 1
            picAlbumDao.update(picAlbumDataList[counterBean.index].picAlbumData)
            picAlbumListAdapter.notifyDataSetChanged()
        }
        if (viewHolder != null) {
            viewHolder.downloadProcessBar.percent = counterBean.curr * 100 / counterBean.max
            viewHolder.downloadProcessBar.postInvalidate()
        }
        Log.d(
            TAG,
            "current = " + counterBean.curr + " max = " + counterBean.max
        )
    }


    fun asyncStartDownload(index: Int, position: Int) {
        val (_, serverIndex) = picAlbumDao.getByInnerIndex(index)
        val url = "http://%serverIP:%serverPort/local1000/picContentAjax?id=$serverIndex"
            .replace("%serverIP", EnvArgs.serverIP)
            .replace("%serverPort", EnvArgs.serverPort)
        DownloadPicsTask(this, position, index, downLoadService).execute(url)
    }
}