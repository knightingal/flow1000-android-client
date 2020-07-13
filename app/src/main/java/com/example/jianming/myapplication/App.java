package com.example.jianming.myapplication;

import android.app.Application;

import com.example.jianming.Utils.CrashHandler;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    }


}
