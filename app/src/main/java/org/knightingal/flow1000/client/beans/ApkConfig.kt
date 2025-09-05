package org.knightingal.flow1000.client.beans

data class ApkConfig (
     val applicationId: String,
     val versionCode: Long,
     val versionName: String,
     val apkName: String,
     val downloadUrl: String
)