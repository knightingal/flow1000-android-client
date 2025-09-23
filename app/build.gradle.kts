import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.io.BufferedReader
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
}

buildscript {
    dependencies {
        classpath(libs.okhttp)
    }
}

/*
keytool -genkey -v -keystore key.jks -alias key0 -keyalg RSA -keysize 2048 -validity 10000 -keypass xxxxxx -storepass xxxxxx
 */
var keystorePropertiesFile = rootProject.file("../keys/keystore.properties")
var keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

fun releaseTime(): String = SimpleDateFormat("yyMMdd").format(Date())
fun versionCode(): Int = SimpleDateFormat("yyMMdd0HH").format(Date()).toInt()
//fun versionCode(): Int = 10
fun commitNum(): String {
    val resultArray = "git describe --always".execute().text().trim().split("-")
    return resultArray[resultArray.size - 1]
}

fun String.execute(): Process {
    val runtime = Runtime.getRuntime()
    return runtime.exec(this)
}

fun Process.text(): String {
    val inputStream = this.inputStream
    val insReader = InputStreamReader(inputStream)
    val bufReader = BufferedReader(insReader)
    var output = ""
    var line: String = ""
    line = bufReader.readLine()
    output += line
    return output
}

android {
    namespace = "org.knightingal.flow1000.client"
    compileSdk = 36

    signingConfigs {
        getByName("debug") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    defaultConfig {
        applicationId = "org.knightingal.flow1000.client"
        minSdk = 26
        targetSdk = 36
        versionCode = versionCode()
        versionName = "${releaseTime()}-${commitNum()}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            // Filter for architectures supported by Flutter
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "PASSWORD", "\""+keystoreProperties["imgPassword"] as String+"\"")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "PASSWORD", "\""+keystoreProperties["imgPassword"] as String+"\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
    }
}

tasks.register("releaseUpload", fun Task.() {
    dependsOn("assembleRelease")
    doLast {
        println("do releaseUpload")
        val target = "${project.buildDir}/outputs/apk/release/app-release.apk"
        println(target)
        val client: OkHttpClient = OkHttpClient().newBuilder().build();
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", target,
                File(target).asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
            .build()
        val request = Request.Builder()
            .url("http://localhost:8000/apkConfig/upload")
            .method("POST", body)
            .build()
        val response = client.newCall(request).execute()
        println("${response.code.toString()}  ${response.body.string()}")
    }
})

dependencies {

//    implementation(libs.jackson.databind)
//    implementation(libs.jackson.module.kotlin)

    implementation(libs.gson)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)

    implementation(libs.guava)
    implementation(libs.flexbox)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.room.runtime)
    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp(libs.androidx.room.compiler)
    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
//    // optional - RxJava2 support for Room
//    implementation(libs.androidx.room.rxjava2)
//    // optional - RxJava3 support for Room
//    implementation(libs.androidx.room.rxjava3)
//    // optional - Guava support for Room, including Optional and ListenableFuture
//    implementation(libs.androidx.room.guava)
    // optional - Test helpers
    testImplementation(libs.androidx.room.testing)
    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)

    implementation(project(":flutter"))

    // android default dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}