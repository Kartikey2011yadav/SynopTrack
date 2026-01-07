package com.example.synoptrack.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

interface LocationService {
    fun requestLocationUpdates(): Flow<Location>
}

@Singleton
class LocationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationService {

    private val client: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L).apply {
            setMinUpdateIntervalMillis(1000L)
            setWaitForAccurateLocation(false)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.lastOrNull()?.let { location ->
                    trySend(location)
                }
            }
        }

        try {
            client.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            client.removeLocationUpdates(locationCallback)
        }
    }
}
