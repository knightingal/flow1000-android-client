package com.example.jianming.myapplication

import SERVER_IP
import SERVER_PORT
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.beans.ApkConfig
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AboutActivity : AppCompatActivity() {

    private var versionCode: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val versionCodeText = findViewById<TextView>(R.id.version_code)
        val versionNameText = findViewById<TextView>(R.id.version_name)
        val imageView = findViewById<ImageView>(R.id.image_view_logo)
        val packageManager = packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            versionNameText.text = versionName
            versionCode = packageInfo.longVersionCode
            versionCodeText.text = versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }

        val mapper = jacksonObjectMapper()
        imageView.setOnClickListener {
            val pendingUrl = "http://${SERVER_IP}:${SERVER_PORT}/apkConfig/newest/package/${packageName}"
            MainScope().launch {
                val respBody = ConcurrencyJsonApiTask.makeRequest(pendingUrl)
                val apkConfig: ApkConfig = mapper.readValue(respBody)
                Log.d("about", apkConfig.toString())
                if (apkConfig.versionCode > versionCode) {
                    Toast.makeText(this@AboutActivity, "you have newer apk", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@AboutActivity, "you are in newest apk", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
