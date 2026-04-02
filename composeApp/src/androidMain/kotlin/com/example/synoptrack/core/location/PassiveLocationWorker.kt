package com.example.synoptrack.core.location

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.synoptrack.core.presence.domain.repository.PresenceRepository
import com.example.synoptrack.core.presence.service.PresenceForegroundService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull

@HiltWorker
class PassiveLocationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val locationService: LocationService,
    private val presenceRepository: PresenceRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Get single location update with timeout
            val location = withTimeoutOrNull(10000L) {
                locationService.requestLocationUpdates().firstOrNull()
            }

            if (location != null) {
                // Determine if we should also check battery? 
                // For passive, we might skip battery to save more power or just get it quickly
                // Reusing the service's logic might be cleaner but service is for foreground.
                // We'll just pass -1 for battery in passive mode or get it if easy.
                
                // Simple battery check
                val batteryStatus = applicationContext.registerReceiver(null, 
                    android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
                val level = batteryStatus?.let { 
                    val l = it.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1)
                    val s = it.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1)
                    (l * 100 / s.toFloat()).toInt()
                } ?: -1

                presenceRepository.updateLocation(location, level, false) // Assume not charging or don't care
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
