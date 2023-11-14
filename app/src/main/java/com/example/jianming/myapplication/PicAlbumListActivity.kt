package com.example.jianming.myapplication

import SERVER_IP
import SERVER_PORT
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room.databaseBuilder
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.util.AppDataBase
import com.example.jianming.util.NetworkUtil
import com.example.jianming.util.TimeUtil
import com.example.jianming.beans.PicAlbumBean
import com.example.jianming.beans.PicAlbumData
import com.example.jianming.dao.PicAlbumDao
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.listAdapters.PicAlbumListAdapter
import com.example.jianming.services.DownloadService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.nanjing.knightingal.processerlib.RefreshListener

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
        updataStampDao = db.updateStampDao()

        setContentView(R.layout.activity_pic_album_list_activity_md)


        listView = findViewById(R.id.list_view11)
        listView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        listView.layoutManager = mLayoutManager

        picAlbumListAdapter = PicAlbumListAdapter(this)
        picAlbumListAdapter.setDataArray(picAlbumDataList)
        listView.adapter = picAlbumListAdapter


    }

    var downLoadService: DownloadService? = null


    var isBound = false
    private val conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Log.d(TAG, "onServiceConnected")
            isBound = true
            downLoadService = (binder as DownloadService.LocalBinder).getService()
            downLoadService!!.setRefreshListener(
                this@PicAlbumListActivity
            )
            if (isNotExistItemShown && NetworkUtil.isNetworkAvailable(this@PicAlbumListActivity)) {
                startDownloadPicIndex()
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
        bindService(Intent(this, DownloadService::class.java), conn, BIND_AUTO_CREATE)
    }

    private fun startDownloadPicIndex() {
        val updateStamp = updataStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN")
        val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?time_stamp=${updateStamp.updateStamp}"
        Log.d("startDownloadWebPage", stringUrl)
        ConcurrencyJsonApiTask.startDownload(stringUrl) { allBody ->
            val mapper = jacksonObjectMapper()
            db.runInTransaction() {
                val updateStamp = updataStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN")
                updateStamp.updateStamp = TimeUtil.currentTimeFormat()
                updataStampDao.update(updateStamp)
                val picAlbumBeanList: List<PicAlbumBean> = mapper.readValue(allBody)
                picAlbumBeanList.forEach { picAlbumDao.insert(it) }
            }

            refreshFrontPage.invoke()

            val pendingUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=PENDING"
            ConcurrencyJsonApiTask.startDownload(pendingUrl) { pendingBody ->
                val picAlbumBeanList: List<PicAlbumBean> = mapper.readValue(pendingBody)
                if (picAlbumBeanList.isNotEmpty()) {
                    picAlbumBeanList.forEach {
                        picAlbumDao.update(it)
                        asyncStartDownload(it.id, picAlbumDataList.indexOf(picAlbumDataList.stream().filter { item ->
                            item.picAlbumBean.id == it.id
                        }.findFirst().get()));
                    }
                }
            }
            val localUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=LOCAL"
            ConcurrencyJsonApiTask.startDownload(localUrl) { pendingBody ->
                val picAlbumBeanList: List<PicAlbumBean> = mapper.readValue(pendingBody)
                if (picAlbumBeanList.isNotEmpty()) {
                    picAlbumBeanList.forEach {
                        val existAlbum = picAlbumDao.getByInnerIndex(it.id)
                        if (existAlbum.exist != 1) {
                            picAlbumDao.update(it)
                            asyncStartDownload(
                                it.id,
                                picAlbumDataList.indexOf(picAlbumDataList.stream().filter { item ->
                                    item.picAlbumBean.id == it.id
                                }.findFirst().get())
                            );
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
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


    @SuppressLint("NotifyDataSetChanged")
    override fun doRefreshView(position: Int, currCount: Int, max: Int) {
        val viewHolder =
            listView.findViewHolderForAdapterPosition(position) as PicAlbumListAdapter.ViewHolder?
        if (currCount == max) {
            picAlbumDataList[position].picAlbumBean.exist = 1
            picAlbumDao.update(picAlbumDataList[position].picAlbumBean)
            picAlbumListAdapter.notifyDataSetChanged()
        }
        if (viewHolder != null) {
            MainScope().launch {
                viewHolder.downloadProcessBar.visibility = View.VISIBLE
                viewHolder.downloadProcessBar.isIndeterminate = false
                viewHolder.downloadProcessBar.setProgressCompat(currCount, false)
                viewHolder.downloadProcessBar.max = max
                Log.d(TAG, "current = $currCount max = $max")
            }
        }
    }


    fun asyncStartDownload(index: Long, position: Int) {
        downLoadService?.startDownloadAlbum(index, position)
    }

}