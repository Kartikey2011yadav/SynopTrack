package com.example.synoptrack.profile.data.repository

import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: com.google.firebase.storage.FirebaseStorage
) : ProfileRepository {

    override fun getUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val listener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    trySend(profile)
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getUserProfileOnce(uid: String): Result<UserProfile?> {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            if (snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java)
                Result.success(profile)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            firestore.collection("users").document(userProfile.uid)
                .set(userProfile, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createOrUpdateUser(uid: String, email: String, displayName: String?, photoUrl: String?): Result<Unit> {
        return try {
            val docRef = firestore.collection("users").document(uid)
            val snapshot = docRef.get().await()

            val data = mutableMapOf<String, Any>(
                "uid" to uid,
                "email" to email
            )
            if (displayName != null) data["displayName"] = displayName
            if (photoUrl != null) data["avatarUrl"] = photoUrl

            if (!snapshot.exists()) {
                data["createdAt"] = FieldValue.serverTimestamp()
                data["ghostMode"] = false
                data["theme"] = "system"
                data["inviteCode"] = generateInviteCode()
                data["isPrivate"] = false // Default
                data["bio"] = ""
            }

            docRef.set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updateGhostMode(uid: String, isEnabled: Boolean): Result<Unit> {
        return try {
            firestore.collection("users").document(uid)
                .update("ghostMode", isEnabled)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTheme(uid: String, theme: String): Result<Unit> {
        return try {
            firestore.collection("users").document(uid)
                .update("theme", theme)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePicture(uid: String, imageBytes: ByteArray): Result<String> {
        return try {
            val ref = storage.reference.child("users/$uid/profile_pic.webp")
            ref.putBytes(imageBytes).await()
            val downloadUrl = ref.downloadUrl.await()
            
            // Update Firestore immediately
            firestore.collection("users").document(uid)
                .update("avatarUrl", downloadUrl.toString())
                .await()
                
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePrivacy(uid: String, isPrivate: Boolean): Result<Unit> {
        return try {
            firestore.collection("users").document(uid)
                .update("isPrivate", isPrivate)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }
}
