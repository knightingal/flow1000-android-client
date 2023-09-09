package com.example.jianming.myapplication;

import android.app.Application;

import com.example.jianming.util.CrashHandler;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    }


}
