package com.example.jianming.myapplication.ui.main

import SERVER_IP
import SERVER_PORT
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.beans.PicSectionBean
import com.example.jianming.beans.PicSectionData
import com.example.jianming.beans.UpdateStamp
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.listAdapters.PicSectionListAdapter
import com.example.jianming.myapplication.databinding.FragmentPendingBinding
import com.example.jianming.services.DownloadService
import com.example.jianming.util.AppDataBase
import com.example.jianming.util.NetworkUtil
import com.example.jianming.util.TimeUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.nanjing.knightingal.processerlib.RefreshListener

private val TAG = "PendingFragment";


class PendingFragment : Fragment(){
    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentPendingBinding? = null

    private lateinit var picSectionDao: PicSectionDao

    private lateinit var picInfoDao: PicInfoDao

    private lateinit var updataStampDao: UpdataStampDao
    private lateinit var db: AppDataBase
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java)

        db = Room.databaseBuilder(
            (context as Context).applicationContext,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()

        picSectionDao = db.picSectionDao()
        picInfoDao = db.picInfoDao()
        updataStampDao = db.updateStampDao()
    }

    private lateinit var picSectionListAdapter: PicSectionListAdapter

    private var picSectionDataList: MutableList<PicSectionData> = mutableListOf();

    private lateinit var pendingListView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
        val root = binding.root
        pendingListView = binding.listViewPending
        pendingListView.setHasFixedSize(true)

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        pendingListView.layoutManager = mLayoutManager
        picSectionListAdapter = PicSectionListAdapter(context)
        picSectionListAdapter.setDataArray(picSectionDataList)
        pendingListView.adapter = picSectionListAdapter

        return root
    }

    override fun onPause() {
        super.onPause()
        downLoadService?.removeRefreshListener()
        downLoadService = null
        context?.unbindService(conn)
    }

    override fun onStart() {
        super.onStart()
        context?.bindService(
            Intent(context, DownloadService::class.java), conn,
            AppCompatActivity.BIND_AUTO_CREATE
        )
    }

    var downLoadService: DownloadService? = null
    var serviceBound = false
    private val conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Log.d(TAG, "onServiceConnected")
            serviceBound = true
            downLoadService = (binder as DownloadService.LocalBinder).getService()
            downLoadService?.setRefreshListener(
                refreshListener
            )

            // startDownloadPicIndex()
            downLoadService?.startDownloadSectionList();
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")
            downLoadService = null
            serviceBound = false
        }
    }

    private val refreshListener: RefreshListener = object : RefreshListener {



        override fun doRefreshProcess(position: Int, currCount: Int, max: Int) {
            val viewHolder =
                pendingListView.findViewHolderForAdapterPosition(position) as PicSectionListAdapter.ViewHolder?

            if (currCount == max) {
                picSectionDataList[position].picSectionBean.exist = 1
                picSectionDao.update(picSectionDataList[position].picSectionBean)
                picSectionListAdapter.notifyDataSetChanged()
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

        override fun doRefreshList(picSectionBeanList: List<PicSectionBean>) {
            picSectionDataList.clear()
            for (picSectionBean in picSectionBeanList) {
                val picSectionData = PicSectionData(picSectionBean)
                picSectionDataList.add(picSectionData)
            }
            picSectionListAdapter.notifyDataSetChanged()
        }

    }


    private val refreshFrontPage: () -> Unit = {
    }

    private fun getDataSourceFromJsonFile(): List<PicSectionBean> {
        return picSectionDao.getAll().toList()
    }
    private fun startDownloadPicIndex() {
        val updateStamp = updataStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN") as UpdateStamp
        val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?time_stamp=${updateStamp.updateStamp}"
        Log.d("startDownloadWebPage", stringUrl)
        ConcurrencyJsonApiTask.startDownload(stringUrl) { allBody ->
            val mapper = jacksonObjectMapper()
            db.runInTransaction() {
                updateStamp.updateStamp = TimeUtil.currentTimeFormat()
                updataStampDao.update(updateStamp)
                val picSectionBeanList: List<PicSectionBean> = mapper.readValue(allBody)
                picSectionBeanList.forEach { picSectionDao.insert(it) }
            }

            refreshFrontPage.invoke()

            val pendingUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=PENDING"
            ConcurrencyJsonApiTask.startDownload(pendingUrl) { pendingBody ->
                val picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                if (picSectionBeanList.isNotEmpty()) {
                    picSectionBeanList.forEach {
                        picSectionDao.update(it)
                        asyncStartDownload(it.id, picSectionDataList.indexOf(picSectionDataList.stream().filter { item ->
                            item.picSectionBean.id == it.id
                        }.findFirst().get()));
                    }
                }
            }
            val localUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?client_status=LOCAL"
            ConcurrencyJsonApiTask.startDownload(localUrl) { pendingBody ->
                val picSectionBeanList: List<PicSectionBean> = mapper.readValue(pendingBody)
                if (picSectionBeanList.isNotEmpty()) {
                    picSectionBeanList.forEach {
                        val existSection = picSectionDao.getByInnerIndex(it.id)
                        if (existSection.exist != 1) {
                            picSectionDao.update(it)
                            asyncStartDownload(
                                it.id,
                                picSectionDataList.indexOf(picSectionDataList.stream().filter { item ->
                                    item.picSectionBean.id == it.id
                                }.findFirst().get())
                            );
                        }
                    }
                }
            }
        }
    }

    fun asyncStartDownload(index: Long, position: Int) {
        downLoadService?.startDownloadSection(index, position)
    }

}