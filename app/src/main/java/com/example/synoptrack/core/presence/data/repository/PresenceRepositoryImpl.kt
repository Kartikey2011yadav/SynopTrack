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

    override suspend fun updateLocation(location: Location): Result<Unit> {
        val uid = authRepository.currentUser?.uid ?: return Result.failure(Exception("No user logged in"))
        
        return try {
            val geoPoint = GeoPoint(location.latitude, location.longitude)
            val updates = mapOf(
                "lastLocation" to geoPoint,
                "lastActiveAt" to FieldValue.serverTimestamp(),
                // "bearing" to location.bearing, // Optional: Add if needed for car rotation
                // "speed" to location.speed
            )
            
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
