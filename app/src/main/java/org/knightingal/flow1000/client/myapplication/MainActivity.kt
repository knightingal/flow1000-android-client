package org.knightingal.flow1000.client.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import org.knightingal.flow1000.client.myapplication.ui.main.SectionsPagerAdapter
import org.knightingal.flow1000.client.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var onExistSectionPage = false

    private var tabPosition = 0;

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
                Log.d("onTabSelected", "tab.position:${tab?.position}")
                tabPosition = tab?.position ?: 0
                onExistSectionPage = tab?.position == 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d("onTabUnselected", "tab.position:${tab?.position}")
                tabPosition = tab?.position ?: 0
                onExistSectionPage = tab?.position == 1
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("onTabReselected", "tab.position:${tab?.position}")
                tabPosition = tab?.position ?: 0
                onExistSectionPage = tab?.position == 1
            }

        }
        tabs.addOnTabSelectedListener(listener)
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { _ ->
            if (tabPosition == 1) {
//                startActivity(
//                    Flow1000FlutterActivity.createDefaultIntent(this)
//                )
            } else if (tabPosition == 0) {
                startActivity(Intent(this, StableDiffusionRepoActivity::class.java))
            } else {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
    }
}