package com.example.jianming.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jianming.myapplication.databinding.ActivityLocal1kBinding

class Local1KActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocal1kBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocal1kBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.local1000.setOnClickListener {
            startActivity(Intent().setClass(this, PicSectionListActivity::class.java))
        }

        binding.offline.setOnClickListener {
            val intent = Intent()
                .setClass(this, PicSectionListActivity::class.java)
                .putExtra("offline", true)
            startActivity(intent)
        }

    }
}