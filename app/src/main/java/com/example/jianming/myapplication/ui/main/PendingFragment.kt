package com.example.jianming.myapplication.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.jianming.util.AppDataBase

class PendingFragment : Fragment(){
    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentPendingBinding? = null

    private lateinit var picSectionDao: PicSectionDao

    private lateinit var picInfoDao: PicInfoDao

    private lateinit var updateStampDao: UpdataStampDao
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
        updateStampDao = db.updateStampDao()
    }

    private lateinit var picSectionListAdapter: PicSectionListAdapter

    private var picSectionDataList: MutableList<PicSectionData> = mutableListOf();
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
        val root = binding.root
        val pendingListView = binding.listViewPending
        pendingListView.setHasFixedSize(true)

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        pendingListView.layoutManager = mLayoutManager
        picSectionListAdapter = PicSectionListAdapter(context)
        picSectionListAdapter.setDataArray(picSectionDataList)
        pendingListView.adapter = picSectionListAdapter

        val dataSourceList = picSectionDao.getAll().toList()
        picSectionDataList.addAll(dataSourceList.map { PicSectionData(it) })
        return root
    }
}