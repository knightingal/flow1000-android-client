import com.android.build.api.dsl.ViewBinding
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

buildscript {
    dependencies{

        classpath("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    }
}

fun releaseTime(): String = SimpleDateFormat("yyMMdd").format(Date())

fun versionCode(): Int = SimpleDateFormat("yyMMdd0HH").format(Date()).toInt()

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

var keystorePropertiesFile = rootProject.file("keystore.properties")
var keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

var localPropertiesFile = rootProject.file("local.properties")
var localProperties = Properties()
localProperties.load(FileInputStream(localPropertiesFile))

android {
    buildFeatures {
        buildConfig = true
    }
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
    namespace = "com.example.jianming.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.flow1000client"
        minSdk = 29
        targetSdk = 34
        versionCode = versionCode()
        versionName = "${releaseTime()}-${commitNum()}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "password", "\"${localProperties["password"] as String}\"")
        }
        release {
            buildConfigField("String", "password", "\"${localProperties["password"] as String}\"")
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    val viewBindingFun : ViewBinding.() -> Unit = {
        enable = true
    }
    viewBinding (viewBindingFun)


}


task("releaseUpload") {
    dependsOn("assembleRelease")
    doLast {
        println("do releaseUpload")
        val target = "${project.buildDir}/outputs/apk/release/app-release.apk"
        println(target)
        val client:OkHttpClient = OkHttpClient().newBuilder().build();
        val mediaType = "text/plain".toMediaTypeOrNull()
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file", target,
                File(target).asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
            .build()
        val request = Request.Builder()
            .url("http://localhost:8001/apkConfig/upload")
            .method("POST", body)
            .build()
        val response = client.newCall(request).execute()
        println("${response.code.toString()}  ${response.body.string()}")




    }
}



dependencies {

    implementation("com.nostra13.universalimageloader:universal-image-loader:1.9.5")


    val jacksonVersion = "2.15.2"
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")

    implementation( "com.google.code.gson:gson:2.10.1")

    implementation("com.google.guava:guava:32.0.1-android")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    val roomVersion = "2.5.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-rxjava2:$roomVersion")
    implementation("androidx.room:room-rxjava3:$roomVersion")
    implementation("androidx.room:room-guava:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
}