package com.example.synoptrack.core.presence.domain.repository

import android.location.Location

interface PresenceRepository {
    suspend fun updateLocation(location: Location): Result<Unit>
    suspend fun setOnlineStatus(isOnline: Boolean): Result<Unit>
}
