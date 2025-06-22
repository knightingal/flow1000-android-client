package com.example.jianming.myapplication

import android.content.Context
import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class Flow1000FlutterActivity : FlutterActivity() {
    private val CHANNEL = "flutter/startWeb"

    companion object {
        fun createDefaultIntent(launchContext: Context): Intent {
            return Flow1000FlutterActivity.withNewEngine().build(launchContext)
        }

        private fun withNewEngine(): NewEngineIntentBuilder {
            return NewEngineIntentBuilder(Flow1000FlutterActivity::class.java)
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call, result ->

            if (call.method == "aboutPage") {
                result.success(5)

            }
        }
    }
}