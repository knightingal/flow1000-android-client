package org.knightingal.flow1000.client.myapplication

import org.knightingal.flow1000.client.util.SERVER_IP
import org.knightingal.flow1000.client.util.SERVER_PORT
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room.databaseBuilder
import com.google.gson.Gson
import org.knightingal.flow1000.client.task.ConcurrencyJsonApiTask
import org.knightingal.flow1000.client.util.AppDataBase
import org.knightingal.flow1000.client.util.NetworkUtil
import org.knightingal.flow1000.client.util.TimeUtil
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.beans.PicSectionData
import org.knightingal.flow1000.client.beans.UpdateStamp
import org.knightingal.flow1000.client.dao.PicSectionDao
import org.knightingal.flow1000.client.dao.PicInfoDao
import org.knightingal.flow1000.client.dao.UpdateStampDao
import org.knightingal.flow1000.client.listAdapters.PicSectionListAdapter
import org.knightingal.flow1000.client.services.DownloadService
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.fasterxml.jackson.module.kotlin.readValue
import org.nanjing.knightingal.processerlib.RefreshListener
import org.knightingal.flow1000.client.R

class PicSectionListActivity : AppCompatActivity(), RefreshListener {

    companion object {
        private const val TAG = "PicSectionListActivityMD"
    }
    private lateinit var db: AppDataBase

    private lateinit var picSectionDao: PicSectionDao

    private lateinit var picInfoDao: PicInfoDao

    private lateinit var updateStampDao: UpdateStampDao


    private lateinit var picSectionListAdapter: PicSectionListAdapter

    private lateinit var listView: RecyclerView

    private var picSectionDataList: MutableList<PicSectionData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val offline = intent.getBooleanExtra("offline", false)
        isNotExistItemShown = !offline

        db = databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()

        picSectionDao = db.picSectionDao()
        picInfoDao = db.picInfoDao()
        updateStampDao = db.updateStampDao()

        setContentView(R.layout.activity_pic_section_list_activity_md)


        listView = findViewById(R.id.list_view11)
        listView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        listView.layoutManager = mLayoutManager

        picSectionListAdapter = PicSectionListAdapter(this)
        picSectionListAdapter.setDataArray(picSectionDataList)
        listView.adapter = picSectionListAdapter


    }

    var downLoadService: DownloadService? = null

    var isBound = false
    private val conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Log.d(TAG, "onServiceConnected")
            isBound = true
            downLoadService = (binder as DownloadService.LocalBinder).getService()
            downLoadService!!.setRefreshListener(
                this@PicSectionListActivity
            )
            if (isNotExistItemShown && NetworkUtil.isNetworkAvailable(this@PicSectionListActivity)) {
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
        downLoadService?.removeRefreshListener(this)
        downLoadService = null
        unbindService(conn)
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, DownloadService::class.java), conn, BIND_AUTO_CREATE)
    }

    private fun startDownloadPicIndex() {
        val updateStamp = updateStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN") as UpdateStamp
        val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?time_stamp=${updateStamp.updateStamp}"
        Log.d("startDownloadWebPage", stringUrl)
        ConcurrencyJsonApiTask.startGet(stringUrl) { allBody ->
//            val mapper = jacksonObjectMapper()
            db.runInTransaction {
                updateStamp.updateStamp = TimeUtil.currentTimeFormat()
                updateStampDao.update(updateStamp)
                val picSectionBeanList = Gson().fromJson(allBody, Array<PicSectionBean>::class.java)
                picSectionBeanList.forEach { picSectionDao.insert(it) }
            }

            refreshFrontPage.invoke()

            val pendingUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=PENDING"
            ConcurrencyJsonApiTask.startGet(pendingUrl) { pendingBody ->
                val picSectionBeanList = Gson().fromJson(pendingBody, Array<PicSectionBean>::class.java)
                if (picSectionBeanList.isNotEmpty()) {
                    picSectionBeanList.forEach {
                        picSectionDao.update(it)
                        asyncStartDownload()
                    }
                }
            }
            val localUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=LOCAL"
            ConcurrencyJsonApiTask.startGet(localUrl) { pendingBody ->
                val picSectionBeanList = Gson().fromJson(pendingBody, Array<PicSectionBean>::class.java)
                if (picSectionBeanList.isNotEmpty()) {
                    picSectionBeanList.forEach {
                        val existSection = picSectionDao.getByInnerIndex(it.id)
                        if (existSection.exist != 1) {
                            picSectionDao.update(it)
                            asyncStartDownload()
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private val refreshFrontPage: () -> Unit = {
        picSectionDataList.clear()
        picSectionListAdapter.notifyDataSetChanged()

    }

    private var isNotExistItemShown = true


    @SuppressLint("NotifyDataSetChanged")
    override fun doRefreshProcess(sectionId: Long, finish: Boolean) {
    }

    override fun doRefreshList(picSectionBeanList: List<PicSectionData>) {
        TODO("Not yet implemented")
    }

    override fun notifyListReady() {
        TODO("Not yet implemented")
    }


    private fun asyncStartDownload() {
    }

}