package com.example.synoptrack.core.presence.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.synoptrack.R
import com.example.synoptrack.core.location.LocationService
import com.example.synoptrack.core.presence.domain.repository.PresenceRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class PresenceForegroundService : Service() {

    @Inject
    lateinit var locationService: LocationService

    @Inject
    lateinit var presenceRepository: PresenceRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_CONVOY -> start()
            ACTION_STOP_CONVOY -> stop()
        }
        return START_STICKY
    }

    private var lastUpdateTimestamp = 0L
    private var lastLocation: android.location.Location? = null

    private fun start() {
        val notification = createNotification()
        startForeground(1, notification)

        locationService.requestLocationUpdates()
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val currentTime = System.currentTimeMillis()
                val timeDiff = currentTime - lastUpdateTimestamp
                val distanceDiff = lastLocation?.distanceTo(location) ?: Float.MAX_VALUE

                // Throttle: Update only if >10s passed OR moved >15m
                if (timeDiff > 10000L || distanceDiff > 15f) {
                    val (level, charging) = getBatteryStatus()
                    presenceRepository.updateLocation(location, level, charging)
                    lastUpdateTimestamp = currentTime
                    lastLocation = location
                }
            }
            .launchIn(serviceScope)
    }

    private fun getBatteryStatus(): Pair<Int, Boolean> {
        val batteryStatus: Intent? = android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            registerReceiver(null, ifilter)
        }
        val level: Int = batteryStatus?.let { intent ->
            val level = intent.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1)
            (level * 100 / scale.toFloat()).toInt()
        } ?: -1
        val status: Int = batteryStatus?.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == android.os.BatteryManager.BATTERY_STATUS_CHARGING ||
                status == android.os.BatteryManager.BATTERY_STATUS_FULL
        return Pair(level, isCharging)
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun createNotification(): Notification {
        val channelId = "presence_channel"
        val channelName = "Convoy Active"
        val manager = getSystemService(NotificationManager::class.java)
        
        // Ensure channel exists (Oreo+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Convoy Active")
            .setContentText("Sharing real-time location with group...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_START_CONVOY = "ACTION_START_CONVOY"
        const val ACTION_STOP_CONVOY = "ACTION_STOP_CONVOY"
    }
}
