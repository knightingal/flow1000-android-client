package com.example.jianming.myapplication

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.jianming.myapplication.ui.main.SectionsPagerAdapter
import com.example.jianming.myapplication.databinding.ActivityMainBinding

//import io.flutter.embedding.android.FlutterActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val listener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.e("onTabSelected","Not yet implemented")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.e("onTabUnselected","Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.e("onTabReselected","Not yet implemented")
            }

        }
        tabs.addOnTabSelectedListener(listener)
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { _ ->
//            startActivity(Intent(this, AboutActivity::class.java))
            startActivity(
                Flow1000FlutterActivity.createDefaultIntent(this, null)
            )
        }
    }
}