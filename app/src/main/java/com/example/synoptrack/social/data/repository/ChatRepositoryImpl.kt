package com.example.synoptrack.social.data.repository

import com.example.synoptrack.social.domain.model.Message
import com.example.synoptrack.social.domain.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun getMessages(groupId: String): Flow<List<Message>> = callbackFlow {
        val query = firestore.collection("groups").document(groupId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val messages = snapshot.toObjects(Message::class.java)
                trySend(messages)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(groupId: String, message: Message): Result<Unit> {
        return try {
            // Let Firestore generate ID if empty, or use provided
            val docRef = if (message.id.isEmpty()) {
                firestore.collection("groups").document(groupId).collection("messages").document()
            } else {
                firestore.collection("groups").document(groupId).collection("messages").document(message.id)
            }
            
            val messageWithId = message.copy(id = docRef.id)
            docRef.set(messageWithId).await()
            
            // Optional: Update group "lastMessage" field for list previews
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
