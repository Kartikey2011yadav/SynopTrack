package com.example.synoptrack.social.domain.repository

import com.example.synoptrack.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    suspend fun createGroup(name: String, ownerId: String): Result<String>
    suspend fun joinGroup(inviteCode: String, userId: String): Result<Unit>
    suspend fun leaveGroup(groupId: String, userId: String): Result<Unit>
    fun getGroupMembers(groupId: String): Flow<List<UserProfile>>
    fun getUserGroups(userId: String): Flow<List<com.example.synoptrack.social.domain.model.Group>>
}
