package com.example.synoptrack.social.domain.repository

import com.example.synoptrack.profile.domain.model.NotificationEntity
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.social.domain.model.FriendRequest
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    suspend fun sendFriendRequest(currentUserId: String, targetUserId: String): Result<Boolean>
    suspend fun acceptFriendRequest(requestId: String): Result<Boolean>
    suspend fun rejectFriendRequest(requestId: String): Result<Boolean>
    
    // Helper methods for UI where we only have User IDs
    suspend fun acceptFriendRequestByUserId(currentUserId: String, senderUserId: String): Result<Boolean>
    suspend fun cancelFriendRequestByUserId(currentUserId: String, receiverUserId: String): Result<Boolean>
    suspend fun removeFriend(currentUserId: String, targetUserId: String): Result<Boolean>
    
    fun getPendingRequests(userId: String): Flow<List<FriendRequest>>
    fun getFriends(userId: String): Flow<List<UserProfile>>
    fun getNotifications(userId: String): Flow<List<NotificationEntity>>
    
    // Search
    suspend fun searchUsers(nameQuery: String, discriminatorQuery: String = ""): Result<List<UserProfile>>
    suspend fun getUserByInviteCode(code: String): Result<UserProfile?>

    suspend fun markNotificationsAsRead(userId: String): Result<Boolean>
}
