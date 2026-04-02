package com.example.synoptrack.social.domain.repository

import com.example.synoptrack.social.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(groupId: String): Flow<List<Message>>
    suspend fun sendMessage(groupId: String, message: Message): Result<Unit>
    
    // New Methods for 1:1 Chats
    fun getConversations(userId: String): Flow<List<com.example.synoptrack.social.data.model.ConversationEntity>>
    fun getConversation(conversationId: String): Flow<com.example.synoptrack.social.data.model.ConversationEntity?>
    suspend fun startConversation(targetUserId: String): Result<String> // Returns chatId
    suspend fun getConversationId(targetUserId: String): String
    suspend fun markAsRead(conversationId: String, userId: String): Result<Unit>
}
