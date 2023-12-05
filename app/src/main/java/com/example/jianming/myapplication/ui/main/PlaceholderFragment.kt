package com.example.jianming.myapplication.ui.main

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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.jianming.Tasks.SectionWorker
import com.example.jianming.myapplication.R
import com.example.jianming.myapplication.databinding.FragmentMainBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
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
            startWork()
        }
        return root
    }

    private fun startWork() {
        val context: Context = context as Context
        val workRequest0 = OneTimeWorkRequestBuilder<SectionWorker>().build()
        val workRequest1 = OneTimeWorkRequestBuilder<SectionWorker>().build()
        val workRequest2 = OneTimeWorkRequestBuilder<SectionWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest0)
        WorkManager.getInstance(context).enqueue(workRequest1)
        WorkManager.getInstance(context).enqueue(workRequest2)

        val workInfoById: ListenableFuture<WorkInfo> = WorkManager.getInstance(context).getWorkInfoById(workRequest0.id)

        Futures.addCallback(workInfoById, object : FutureCallback<WorkInfo> {
            override fun onSuccess(result: WorkInfo?) {
//                TODO("Not yet implemented")
                Log.d("main", "call back")
            }

            override fun onFailure(t: Throwable) {
//                TODO("Not yet implemented")
            }

        }, Executors.newSingleThreadExecutor())

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest0.id)
            .observe(viewLifecycleOwner) {
                workInfo ->
                Log.d("main", "workInfoById0 status change to ${workInfo.state}")
            }
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest1.id)
            .observe(viewLifecycleOwner) {
                    workInfo ->
                Log.d("main", "workInfoById1 status change to ${workInfo.state}")
            }
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest2.id)
            .observe(viewLifecycleOwner) {
                    workInfo ->
                Log.d("main", "workInfoById2 status change to ${workInfo.state}")
            }
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