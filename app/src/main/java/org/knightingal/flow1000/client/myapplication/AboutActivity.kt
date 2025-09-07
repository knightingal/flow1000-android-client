package org.knightingal.flow1000.client.myapplication

import SERVER_IP
import SERVER_PORT
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import org.knightingal.flow1000.client.Tasks.ConcurrencyApkTask
import org.knightingal.flow1000.client.Tasks.ConcurrencyJsonApiTask
import org.knightingal.flow1000.client.beans.ApkConfig
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import androidx.core.net.toUri
import com.google.gson.Gson
import org.knightingal.flow1000.client.R


class AboutActivity : AppCompatActivity() {

    private var versionCode: Long = 0

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private lateinit var apkFile: File
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val versionCodeText = findViewById<TextView>(R.id.version_code)
        val versionNameText = findViewById<TextView>(R.id.version_name)
        val imageView = findViewById<ImageView>(R.id.image_view_logo)
        val packageManager = packageManager


        launcher = registerForActivityResult(
            object: ActivityResultContract<Intent, Boolean>() {
                override fun createIntent(context: Context, input: Intent) = input
                override fun parseResult(resultCode: Int, intent: Intent?) = resultCode == RESULT_OK
            }
        ) {
            when (it) {
                true -> openAPKFile()
                else -> Toast.makeText(this@AboutActivity,
                        "you did not grant the permission",
                        Toast.LENGTH_LONG)
                        .show()
            }
        }



        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            versionNameText.text = versionName
            versionCode = packageInfo.longVersionCode
            versionCodeText.text = versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }

//        val mapper = jacksonObjectMapper()
        imageView.setOnClickListener {
            val pendingUrl = "http://${SERVER_IP}:${SERVER_PORT}/apkConfig/newest/package/${packageName}"
            MainScope().launch {
                val respBody = ConcurrencyJsonApiTask.makeRequest(pendingUrl)
                val apkConfig: ApkConfig = Gson().fromJson(respBody, ApkConfig::class.java)
                Log.d("about", apkConfig.toString())
                if (apkConfig.versionCode > versionCode) {
                    Toast.makeText(this@AboutActivity, "you have newer apk", Toast.LENGTH_LONG).show()
                    val directory = File(this@AboutActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "apk")
                    apkFile = File(directory, apkConfig.apkName)
                    directory.mkdirs()
                    ConcurrencyApkTask.makeRequest(apkConfig.downloadUrl, apkFile)

                    if (getPackageManager().canRequestPackageInstalls()) {
                        openAPKFile()
                    } else {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, "package:$packageName".toUri()
                        )
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        launcher.launch(intent)
                    }
                } else {
                    Toast.makeText(this@AboutActivity, "you are in newest apk", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    private fun openAPKFile() {
        val mimeDefault = "application/vnd.android.package-archive"
        Log.d("file", "file path: $apkFile")
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val contentUri = FileProvider.getUriForFile(
                this@AboutActivity,
                "com.example.flow1000client.file_provider",
                apkFile
            )
            intent.setDataAndType(contentUri, mimeDefault)
            startActivity(intent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
