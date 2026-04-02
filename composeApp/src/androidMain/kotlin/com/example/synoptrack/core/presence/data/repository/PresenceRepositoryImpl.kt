package com.example.synoptrack.core.presence.data.repository

import android.location.Location
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.core.presence.domain.repository.PresenceRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import javax.inject.Inject

class PresenceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : PresenceRepository {

    override suspend fun updateLocation(location: Location, batteryLevel: Int?, isCharging: Boolean?): Result<Unit> {
        val uid = authRepository.currentUser?.uid ?: return Result.failure(Exception("No user logged in"))
        
        return try {
            val geoPoint = GeoPoint(location.latitude, location.longitude)
            val updates = mutableMapOf<String, Any>(
                "lastLocation" to geoPoint,
                "lastActiveAt" to FieldValue.serverTimestamp()
            )
            
            if (batteryLevel != null) updates["batteryLevel"] = batteryLevel
            if (isCharging != null) updates["isCharging"] = isCharging
            
            // Assuming 'users' is the collection
            firestore.collection("users").document(uid).update(updates)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setOnlineStatus(isOnline: Boolean): Result<Unit> {
        val uid = authRepository.currentUser?.uid ?: return Result.failure(Exception("No user logged in"))
        return try {
             val updates = mapOf(
                "isOnline" to isOnline,
                "lastActiveAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(uid).update(updates)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
