package com.example.jianming.myapplication

import SERVER_IP
import SERVER_PORT
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.jianming.Tasks.ConcurrencyApkTask
import com.example.jianming.Tasks.ConcurrencyJsonApiTask
import com.example.jianming.beans.ApkConfig
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File


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
                    val directory = File(this@AboutActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "apk")
                    apkFile = File(directory, apkConfig.apkName)
                    directory.mkdirs()
                    ConcurrencyApkTask.makeRequest(apkConfig.downloadUrl, apkFile)

                    val intent = Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse(
                            "package:$packageName"
                        )
                    )
                    startActivityForResult(intent, 1)
                } else {
                    Toast.makeText(this@AboutActivity, "you are in newest apk", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    lateinit var apkFile: File

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            if (requestCode == 1) {
                openAPKFile()
            }
        }
    }

    private fun openAPKFile() {
        val mimeDefault = "application/vnd.android.package-archive"
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //兼容7.0
            val contentUri = FileProvider.getUriForFile(
                this,
                packageName + ".fileprovider", apkFile
            )
            intent.setDataAndType(contentUri, mimeDefault)
                //如果APK安装界面存在，携带请求码跳转。使用forResult是为了处理用户 取消 安装的事件。外面这层判断理论上来说可以不要，但是由于国内的定制，这个加上还是比较保险的
            startActivityForResult(intent, 2)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
