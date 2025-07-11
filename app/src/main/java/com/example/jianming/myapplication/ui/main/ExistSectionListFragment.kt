package com.example.jianming.myapplication.ui.main

import android.annotation.SuppressLint
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
import com.example.jianming.beans.PicSectionBean
import com.example.jianming.beans.PicSectionData
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.listAdapters.PicSectionListAdapter
import com.example.jianming.listAdapters.PicSectionListAdapter.ItemClickListener
import com.example.jianming.myapplication.Flow1000FlutterActivity
import com.example.jianming.myapplication.SectionImageListActivity
import com.example.jianming.myapplication.databinding.FragmentPendingBinding
import com.example.jianming.myapplication.ui.main.PendingFragment.Companion
import com.example.jianming.services.DownloadService
import com.example.jianming.util.AppDataBase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.nanjing.knightingal.processerlib.RefreshListener

class ExistSectionListFragment : Fragment(){

    companion object {
        private const val TAG = "ExistSectionListFragment"
    }
    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentPendingBinding? = null

    private lateinit var picSectionDao: PicSectionDao

    private lateinit var picInfoDao: PicInfoDao

    private lateinit var updateStampDao: UpdataStampDao
    private lateinit var db: AppDataBase
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this)[PageViewModel::class.java]

        db = Room.databaseBuilder(
            (context as Context).applicationContext,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()

        picSectionDao = db.picSectionDao()
        picInfoDao = db.picInfoDao()
        updateStampDao = db.updateStampDao()
    }

    private lateinit var picSectionListAdapter: PicSectionListAdapter

    private var picSectionDataList: List<PicSectionData> = listOf()

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
        val itemClickListener = ItemClickListener {
            if (it.picSectionBean.clientStatus == PicSectionBean.ClientStatus.LOCAL) {
                val intent = Flow1000FlutterActivity.createDefaultIntent(context as Context)
                startActivity(intent)
            }
        }
        picSectionListAdapter = PicSectionListAdapter(context)
        picSectionListAdapter.setItemClickListener(itemClickListener)
        pendingListView.adapter = picSectionListAdapter

        return root
    }

    private var serviceBound = false

    private var downLoadService: DownloadService? = null

    private val conn: ServiceConnection = object : ServiceConnection {
        @SuppressLint("NotifyDataSetChanged")
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Log.d(TAG, "onServiceConnected")
            serviceBound = true
            downLoadService = (binder as DownloadService.LocalBinder).getService()
            downLoadService?.setRefreshListener(
                refreshListener
            )
            val picSectionBeanList = downLoadService?.getPendingSectionList()
            picSectionListAdapter.setDataArray(picSectionBeanList)
            picSectionListAdapter.notifyDataSetChanged()

        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")
            downLoadService?.setRefreshListener(null)
            downLoadService = null
            serviceBound = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()

        super.onStart()
        context?.bindService(
            Intent(context, DownloadService::class.java), conn,
            AppCompatActivity.BIND_AUTO_CREATE
        )

        picSectionDataList = picSectionDao.getByClientStatus(PicSectionBean.ClientStatus.LOCAL).map { bean -> PicSectionData(bean, 0) }
        picSectionListAdapter.setDataArray(picSectionDataList)
        picSectionListAdapter.notifyDataSetChanged()
    }


    private val refreshListener: RefreshListener = object : RefreshListener {
        @SuppressLint("SetTextI18n")
        override fun doRefreshProcess(sectionId:Long,  finish: Boolean) {
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun doRefreshList(picSectionBeanList: List<PicSectionData>) {
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun notifyListReady() {
            picSectionDataList = picSectionDao.getByClientStatus(PicSectionBean.ClientStatus.LOCAL).map { bean -> PicSectionData(bean, 0) }
            picSectionListAdapter.setDataArray(picSectionDataList)
            picSectionListAdapter.notifyDataSetChanged()
        }
    }
}