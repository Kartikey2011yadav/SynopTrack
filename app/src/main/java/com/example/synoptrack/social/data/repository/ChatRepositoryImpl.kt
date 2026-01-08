package com.example.synoptrack.social.data.repository

import com.example.synoptrack.social.domain.model.Message
import com.example.synoptrack.social.domain.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@javax.inject.Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val chatDao: com.example.synoptrack.core.database.dao.ChatMessageDao
) : ChatRepository {

    override fun getMessages(groupId: String): Flow<List<Message>> {
        // 1. Trigger background sync
        syncMessages(groupId)

        // 2. Return local data immediately
        return chatDao.getMessages(groupId).map { entities ->
            entities.map { entity ->
                Message(
                    id = entity.id,
                    senderId = entity.senderId,
                    senderName = entity.senderName,
                    content = entity.content,
                    timestamp = java.util.Date(entity.timestamp),
                    type = entity.type
                )
            }
        }
    }

    private fun syncMessages(groupId: String) {
        val query = firestore.collection("groups").document(groupId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)

        query.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val messages = snapshot.toObjects(Message::class.java)
                val entities = messages.map { msg ->
                    com.example.synoptrack.core.database.entity.ChatMessageEntity(
                        id = msg.id,
                        groupId = groupId,
                        senderId = msg.senderId,
                        senderName = msg.senderName,
                        content = msg.content,
                        timestamp = msg.timestamp?.time ?: System.currentTimeMillis(),
                        type = msg.type
                    )
                }
                // IO operation in background
                kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    chatDao.insertMessages(entities)
                }
            }
        }
    }

    override suspend fun sendMessage(groupId: String, message: Message): Result<Unit> {
        return try {
            val docRef = if (message.id.isEmpty()) {
                firestore.collection("groups").document(groupId).collection("messages").document()
            } else {
                firestore.collection("groups").document(groupId).collection("messages").document(message.id)
            }
            
            val messageWithId = message.copy(id = docRef.id)
            docRef.set(messageWithId).await()
            
            // Optimistic update (optional, but good for UX)
            // chatDao.insertMessage(entity) // Can be done if we want instant local echo before server confirms
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
