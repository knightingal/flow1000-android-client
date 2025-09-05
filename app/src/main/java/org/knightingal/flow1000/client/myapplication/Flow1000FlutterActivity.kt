package org.knightingal.flow1000.client.myapplication

//import android.content.Context
//import android.content.Intent
//import io.flutter.embedding.android.FlutterActivity
//import io.flutter.embedding.engine.FlutterEngine
//import io.flutter.plugin.common.MethodChannel
//
//private const val CHANNEL = "flutter/flow1000"
//class Flow1000FlutterActivity : FlutterActivity() {
//
//    companion object {
//        fun createDefaultIntent(launchContext: Context): Intent {
//            return withNewEngine()
//                .initialRoute("/")
//                .build(launchContext)
//        }
//
//        private fun withNewEngine(): NewEngineIntentBuilder {
//            return NewEngineIntentBuilder(Flow1000FlutterActivity::class.java)
//        }
//    }
//
//    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
//        super.configureFlutterEngine(flutterEngine)
//        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
//                call, result ->
//
//            if (call.method == "getSectionId") {
//                result.success(5)
//
//            }
//        }
//    }
//}