package com.example.synoptrack.social.domain.repository

import com.example.synoptrack.social.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(groupId: String): Flow<List<Message>>
    suspend fun sendMessage(groupId: String, message: Message): Result<Unit>
}
