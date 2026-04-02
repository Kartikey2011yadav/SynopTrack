package com.example.synoptrack.profile.domain.repository

import com.example.synoptrack.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile(uid: String): Flow<UserProfile?>
    suspend fun getUserProfileOnce(uid: String): Result<UserProfile?>
    suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit>
    suspend fun createOrUpdateUser(uid: String, email: String, displayName: String?, photoUrl: String?): Result<Unit>
    suspend fun updateGhostMode(uid: String, isEnabled: Boolean): Result<Unit>
    suspend fun updateTheme(uid: String, theme: String): Result<Unit>
    suspend fun uploadProfilePicture(uid: String, imageBytes: ByteArray): Result<String>
    suspend fun updatePrivacy(uid: String, isPrivate: Boolean): Result<Unit>
    suspend fun updateFcmToken(token: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun checkIdentityAvailability(username: String, discriminator: String): Result<Boolean>
}
