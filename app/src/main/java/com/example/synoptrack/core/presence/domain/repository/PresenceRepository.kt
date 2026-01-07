package com.example.synoptrack.core.presence.domain.repository

import android.location.Location

interface PresenceRepository {
    suspend fun updateLocation(location: Location, batteryLevel: Int? = null, isCharging: Boolean? = false): Result<Unit>
    suspend fun setOnlineStatus(isOnline: Boolean): Result<Unit>
}
