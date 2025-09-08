package org.knightingal.flow1000.client.myapplication.ui.main

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.beans.PicSectionData
import org.knightingal.flow1000.client.dao.PicInfoDao
import org.knightingal.flow1000.client.dao.PicSectionDao
import org.knightingal.flow1000.client.dao.UpdateStampDao
import org.knightingal.flow1000.client.listAdapters.PicSectionListAdapter
import org.knightingal.flow1000.client.databinding.FragmentPendingBinding
import org.knightingal.flow1000.client.services.DownloadService
import org.knightingal.flow1000.client.util.AppDataBase
import org.nanjing.knightingal.processerlib.RefreshListener

class ExistSectionListFragment : Fragment() {

    companion object {
        private const val TAG = "ExistSectionListFragment"
    }

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

//        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
//        pendingListView.layoutManager = mLayoutManager
//        val itemClickListener = ItemClickListener {
//            if (it.picSectionBean.clientStatus == PicSectionBean.ClientStatus.LOCAL) {
//                val intent = Intent(context, SectionImageListActivity::class.java)
//                    .putExtra("exist", 1)
//                    .putExtra("name", it.picSectionBean.name)
//                    .putExtra("serverIndex", it.picSectionBean.id)
//                startActivity(intent)
//            }
//        }
//        picSectionListAdapter = PicSectionListAdapter(context)
//        picSectionListAdapter.setItemClickListener(itemClickListener)
//        pendingListView.adapter = picSectionListAdapter

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
            val picSectionBeanList = downLoadService?.getPendingSectionList() ?: listOf()
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
//        context?.bindService(
//            Intent(context, DownloadService::class.java), conn,
//            AppCompatActivity.BIND_AUTO_CREATE
//        )
//
//        picSectionDataList = picSectionDao.getByClientStatus(PicSectionBean.ClientStatus.LOCAL).map { bean -> PicSectionData(bean, 0) }
//        picSectionListAdapter.setDataArray(picSectionDataList)
//        picSectionListAdapter.notifyDataSetChanged()
    }


    private val refreshListener: RefreshListener = object : RefreshListener {
        @SuppressLint("SetTextI18n")
        override fun doRefreshProcess(sectionId: Long, finish: Boolean) {
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun doRefreshList(picSectionBeanList: List<PicSectionData>) {
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun notifyListReady() {
            picSectionDataList = picSectionDao.getByClientStatus(PicSectionBean.ClientStatus.LOCAL)
                .map { bean -> PicSectionData(bean, 0) }
            picSectionListAdapter.setDataArray(picSectionDataList)
            picSectionListAdapter.notifyDataSetChanged()
        }
    }
}