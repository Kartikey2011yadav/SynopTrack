package com.example.synoptrack.social.domain.repository

import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.social.domain.model.FriendRequest
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    suspend fun sendFriendRequest(currentUserId: String, targetUserId: String): Result<Boolean>
    suspend fun acceptFriendRequest(requestId: String): Result<Boolean>
    suspend fun rejectFriendRequest(requestId: String): Result<Boolean>
    
    fun getPendingRequests(userId: String): Flow<List<FriendRequest>>
    fun getFriends(userId: String): Flow<List<UserProfile>>
    
    // Search
    suspend fun searchUsers(nameQuery: String, discriminatorQuery: String = ""): Result<List<UserProfile>>
    suspend fun getUserByInviteCode(code: String): Result<UserProfile?>
}
