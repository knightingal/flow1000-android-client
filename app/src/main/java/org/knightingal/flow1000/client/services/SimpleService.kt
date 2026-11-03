package org.knightingal.flow1000.client.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.knightingal.flow1000.client.R
import kotlin.time.Duration.Companion.seconds

class SimpleService : Service() {
  companion object {
    const val TASK_COUNT = 100
  }

  override fun onBind(p0: Intent?): IBinder? {
    Log.d("SimpleService", "onBind called")
    return binder
  }

  override fun onUnbind(intent: Intent?): Boolean {
    Log.d("SimpleService", "onUnbind called")
    return super.onUnbind(intent)
  }

  inner class LocalBinder : Binder() {
    fun getService(): SimpleService = this@SimpleService
  }

  interface NotifyListener {
    fun onNotify(index: Int, message: String)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d("SimpleService", "onStartCommand called")
    val name = "flow1000"
    val descriptionText = "SimpleService is running"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
      description = descriptionText
    }
    // Register the channel with the system.
    val notificationManager: NotificationManager =
      getSystemService(NotificationManager::class.java) as NotificationManager
    notificationManager.createNotificationChannel(channel)

    val notification = NotificationCompat.Builder(this, "CHANNEL_ID")
      .setSmallIcon(R.drawable.app_icon)
      // Create the notification to display while the service is running
      .build()

    ServiceCompat.startForeground(
      /* service = */ this,
      /* id = */ 100, // Cannot be 0
      /* notification = */ notification,
      /* foregroundServiceType = */
      ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
    )
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