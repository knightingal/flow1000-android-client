package org.knightingal.flow1000.client.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SimpleService : Service() {
  companion object {
    const val TASK_COUNT = 100
  }

  override fun onBind(p0: Intent?): IBinder? {
    Log.d("SimpleService", "onBind called")
    return binder
  }

  inner class LocalBinder : Binder() {
    fun getService(): SimpleService = this@SimpleService
  }

  interface NotifyListener {
    fun onNotify(index: Int, message: String)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d("SimpleService", "onStartCommand called")
    startWork()
    return START_STICKY
  }

  var notifyListener: NotifyListener? = null

  fun startWork(): Unit {
    Log.d("SimpleService", "startWork called")
    // Implement your work logic here
    for (i in 0 until TASK_COUNT) {
      CoroutineScope(Dispatchers.IO).launch {
        NotifyEntity(i).startUp()
      }
    }
    Log.d("SimpleService", "startWork done")
  }

  private val binder: IBinder = LocalBinder()

  inner class NotifyEntity(val index: Int) {
    var value = index

    fun inc() {
      value++
      notifyListener?.onNotify(index, "NotifyEntity $index incremented to $value")
    }

    suspend fun startUp() {
      while (true) {
        delay(1.seconds)
        inc()
      }
    }
  }

}