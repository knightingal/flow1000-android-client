package org.knightingal.flow1000.client.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SimpleService : Service() {
  override fun onBind(p0: Intent?): IBinder? {
    Log.d("SimpleService", "onBind called")
    return binder
  }

  inner class LocalBinder : Binder() {
    fun getService(): SimpleService = this@SimpleService
  }

  interface NotifyListener {
    fun onNotify()
  }

  var notifyListener: NotifyListener? = null

  fun startWork(): Unit {
    Log.d("SimpleService", "startWork called")
    // Implement your work logic here
    MainScope().launch {
      while (true) {
        Log.d("SimpleService", "startWork once")
        delay(1.seconds)
        notifyListener?.onNotify()
      }
    }
    Log.d("SimpleService", "startWork done")
  }

  private val binder: IBinder = LocalBinder()

}