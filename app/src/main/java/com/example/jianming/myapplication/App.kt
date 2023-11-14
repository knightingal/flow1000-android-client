package com.example.jianming.myapplication

import android.app.Application
import com.example.jianming.util.CrashHandler

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
    }
}
