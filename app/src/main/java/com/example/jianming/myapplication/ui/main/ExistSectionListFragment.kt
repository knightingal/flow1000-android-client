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
import com.example.jianming.beans.PicSectionData
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.dao.UpdataStampDao
import com.example.jianming.listAdapters.PicSectionListAdapter
import com.example.jianming.myapplication.databinding.FragmentPendingBinding
import com.example.jianming.services.DownloadService
import com.example.jianming.util.AppDataBase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.nanjing.knightingal.processerlib.RefreshListener

class ExistSectionListFragment : Fragment(){
    companion object {
        private const val TAG = "SectionListFragment"
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
        picSectionListAdapter = PicSectionListAdapter(context, null)
        pendingListView.adapter = picSectionListAdapter

        return root
    }

    override fun onPause() {
        super.onPause()
//        downLoadService?.removeRefreshListener(refreshListener)
//        downLoadService = null
//        context?.unbindService(conn)
    }

    override fun onStart() {
        super.onStart()
        picSectionDataList = picSectionDao.getAllExist().map { bean -> PicSectionData(bean, 0) }
        picSectionListAdapter.setDataArray(picSectionDataList)
        picSectionListAdapter.notifyDataSetChanged()
//        context?.bindService(
//            Intent(context, DownloadService::class.java), conn,
//            AppCompatActivity.BIND_AUTO_CREATE
//        )
    }


}