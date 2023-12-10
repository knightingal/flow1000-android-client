package com.example.jianming.myapplication.ui.main

import SERVER_IP
import SERVER_PORT
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.ArrayCreatingInputMerger
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.jianming.Tasks.BaseWorker
import com.example.jianming.Tasks.DownloadCompleteWorker
import com.example.jianming.Tasks.DownloadImageWorker
import com.example.jianming.Tasks.DownloadSectionWorker
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.myapplication.App
import com.example.jianming.myapplication.databinding.FragmentMainBinding
import com.example.jianming.myapplication.getSectionConfig
import com.example.jianming.util.AppDataBase
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executors

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var picSectionDao : PicSectionDao
    private lateinit var db: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
        db = App.findDb()
        picSectionDao = db.picSectionDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        val textView: TextView = binding.sectionLabel
        pageViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        _binding!!.taskBtn.setOnClickListener {
            Log.d("main", "click")
            startWork1(3L)
            startWork1(4L)
        }
        return root
    }

    private fun startWork1(sectionId: Long) {

        val context = context as Context
        val picSectionBean = picSectionDao.getByInnerIndex(sectionId)

        val sectionConfig = getSectionConfig(picSectionBean.album)

        val downloadSectionRequest = OneTimeWorkRequestBuilder<DownloadSectionWorker>()
            .setInputData(workDataOf(
                "sectionId" to sectionId
            )).build()
        WorkManager.getInstance(context).enqueue(downloadSectionRequest)
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(downloadSectionRequest.id)
            .observeForever() { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    Log.d("DownloadService", "worker for $sectionId finish")
                    val pics = workInfo.outputData.getStringArray("pics") as Array<String>
                    val dirName = workInfo.outputData.getString("dirName") as String

                    val imgWorkerList = pics.map { pic ->

                        val imgUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                                "/linux1000/${sectionConfig.baseUrl}/${dirName}/${if (sectionConfig.encryped) "$pic.bin" else pic}"

                        OneTimeWorkRequestBuilder<DownloadImageWorker>()
                            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                            .addTag(imgUrl)
                            .setInputData(workDataOf("imgUrl" to imgUrl,
                                "picName" to pic,
                                "dirName" to dirName,
                                "sectionBeanId" to picSectionBean.id,
                                "encrypted" to sectionConfig.encryped,
                            ))
                            .build()
                    }

                    val beginWith = WorkManager.getInstance(context).beginWith(imgWorkerList)
                    beginWith.workInfosLiveData.observeForever {
                            itList ->
                        val finishCount = itList.count { it -> it.state.isFinished }
                        Log.d("work", "section $sectionId finish $finishCount")
                    }

                    val downloadCompleteWorker = OneTimeWorkRequestBuilder<DownloadCompleteWorker>()
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(
                            workDataOf(
                                "sectionId" to sectionId,
                            )
                        )
                        .build()

                    beginWith.then(downloadCompleteWorker).enqueue();

                }
            }
    }

    private fun startWork() {
        val context: Context = context as Context
        val workRequest0 = OneTimeWorkRequestBuilder<BaseWorker>()
            .addTag("task0")
            .setInputData(workDataOf("sleepTime" to 10L))
            .build()
        val workRequest1 = OneTimeWorkRequestBuilder<BaseWorker>()
            .addTag("task1")
            .setInputData(workDataOf("sleepTime" to 20L))
            .build()
        val workRequest2 = OneTimeWorkRequestBuilder<BaseWorker>()
            .addTag("task2")
            .setInputData(workDataOf("sleepTime" to 30L))
            .build()
        val beginWith = WorkManager.getInstance(context)
            .beginWith(listOf(workRequest0, workRequest1, workRequest2))
        beginWith.workInfosLiveData.observeForever {
            itList ->
            itList.filter { it -> it.state.isFinished }.forEach { it ->
                Log.d("work", "${it.tags.toList()[1]} fininshed")
            }


        }
        beginWith.enqueue()

//        WorkManager.getInstance(context).enqueue(workRequest0)
//        WorkManager.getInstance(context).enqueue(workRequest1)
//        WorkManager.getInstance(context).enqueue(workRequest2)
//
//
//        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest0.id)
//            .observe(viewLifecycleOwner) {
//                workInfo ->
//                Log.d("main", "workInfoById0 status change to ${workInfo.state}")
//            }
//        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest1.id)
//            .observe(viewLifecycleOwner) {
//                    workInfo ->
//                Log.d("main", "workInfoById1 status change to ${workInfo.state}")
//            }
//        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest2.id)
//            .observe(viewLifecycleOwner) {
//                    workInfo ->
//                Log.d("main", "workInfoById2 status change to ${workInfo.state}")
//            }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}