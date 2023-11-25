package com.example.jianming.myapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jianming.myapplication.databinding.FragmentPendingBinding

class PendingFragment : Fragment(){
    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentPendingBinding? = null

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(999)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
        val root = binding.root
        val textView = binding.sectionLabel
        textView.text = pageViewModel.pendingText
        return root
    }
}