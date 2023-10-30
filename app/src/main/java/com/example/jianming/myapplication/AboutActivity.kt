package com.example.jianming.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val versionCodeText = findViewById<TextView>(R.id.version_code)
        val versionNameText = findViewById<TextView>(R.id.version_name)
        val imageView = findViewById<ImageView>(R.id.image_view_logo)
        imageView.setOnClickListener { }
        val packageManager = packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            versionNameText.text = versionName
            val versionCode = packageInfo.longVersionCode
            versionCodeText.text = versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }
    }
}
