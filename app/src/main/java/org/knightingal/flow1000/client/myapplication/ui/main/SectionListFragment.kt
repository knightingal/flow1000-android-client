package org.knightingal.flow1000.client.myapplication.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import org.knightingal.flow1000.client.beans.PicSectionData
import org.knightingal.flow1000.client.dao.PicInfoDao
import org.knightingal.flow1000.client.dao.PicSectionDao
import org.knightingal.flow1000.client.dao.UpdateStampDao
import org.knightingal.flow1000.client.listAdapters.ItemClickListener
import org.knightingal.flow1000.client.listAdapters.PicSectionListAdapter
import org.knightingal.flow1000.client.myapplication.SectionImageListActivity
import org.knightingal.flow1000.client.databinding.FragmentPendingBinding
import org.knightingal.flow1000.client.util.AppDataBase

class SectionListFragment : Fragment(){
    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentPendingBinding? = null

    private lateinit var picSectionDao: PicSectionDao

    private lateinit var picInfoDao: PicInfoDao

    private lateinit var updateStampDao: UpdateStampDao
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

    private var picSectionDataList: MutableList<PicSectionData> = mutableListOf()

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
            val intent = Intent(context, SectionImageListActivity::class.java)
                .putExtra("name", it.picSectionBean.name)
                .putExtra("serverIndex", it.picSectionBean.id)
                .putExtra("exist", it.picSectionBean.exist)
            startActivity(intent)
        }
        picSectionListAdapter = PicSectionListAdapter(requireContext())
        picSectionListAdapter.setDataArray(picSectionDataList)
        picSectionListAdapter.setItemClickListener(itemClickListener)
        pendingListView.adapter = picSectionListAdapter

        return root
    }
}