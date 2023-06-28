import com.android.build.api.dsl.ViewBinding
import java.util.Properties
import java.io.FileInputStream
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
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
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.flow1000client"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

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

dependencies {

    implementation("com.nostra13.universalimageloader:universal-image-loader:1.9.5")


    var jacksonVersion = "2.15.2"
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")

    implementation( "com.google.code.gson:gson:2.10.1")

    implementation("com.google.guava:guava:32.0.1-android")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
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