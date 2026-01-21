package com.example.synoptrack.social.data.repository

import com.example.synoptrack.social.domain.model.Message
import com.example.synoptrack.social.domain.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@javax.inject.Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val chatDao: com.example.synoptrack.core.database.dao.ChatMessageDao
) : ChatRepository {

    private val repositoryScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.SupervisorJob())

    override fun getMessages(groupId: String): Flow<List<Message>> {
        // ... (existing implementation)
        syncMessages(groupId)
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

    @Suppress("UNCHECKED_CAST")
    private fun syncMessages(groupId: String) {
        // ... (existing implementation)
         val query = firestore.collection("conversations").document(groupId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)

        android.util.Log.d("ChatRepo_Debug", "Starting syncMessages for group: $groupId")

        query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                 android.util.Log.e("ChatRepo_Debug", "syncMessages Listen failed", error)
                 return@addSnapshotListener
            }
            if (snapshot != null) {
                android.util.Log.d("ChatRepo_Debug", "syncMessages: Received ${snapshot.size()} messages. (Empty? ${snapshot.isEmpty})")
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
                repositoryScope.launch {
                    chatDao.insertMessages(entities)
                    android.util.Log.d("ChatRepo_Debug", "Inserted ${entities.size} messages into DAO")
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun sendMessage(groupId: String, message: Message): Result<Unit> {
        return try {
            val docRef = if (message.id.isEmpty()) {
                firestore.collection("conversations").document(groupId).collection("messages").document()
            } else {
                firestore.collection("conversations").document(groupId).collection("messages").document(message.id)
            }
            
            val messageWithId = message.copy(id = docRef.id)
            val conversationRef = firestore.collection("conversations").document(groupId)
            
            android.util.Log.d("ChatRepo", "Sending message to group: $groupId with ID: ${docRef.id}")

            firestore.runTransaction { transaction ->
                // 1. Write Message
                transaction.set(docRef, messageWithId)
                
                // 2. Get Current Conversation Data for Unread Counts
                val snapshot = transaction.get(conversationRef)
                
                if (!snapshot.exists()) {
                     android.util.Log.e("ChatRepo", "Conversation doc does not exist for ID: $groupId")
                     // You might want to create it here if missing, but for now just log
                     throw IllegalStateException("Conversation not found")
                }

                val participants = snapshot.get("participants") as? List<String> ?: emptyList()
                val currentUnread = snapshot.get("unreadCounts") as? Map<String, Long> ?: emptyMap()
                
                // 3. Calculate New Unread Counts
                // Increment for everyone EXCEPT sender
                val newUnread = currentUnread.toMutableMap()
                participants.forEach { uid ->
                    if (uid != message.senderId) {
                        val count = newUnread[uid] ?: 0L
                        newUnread[uid] = count + 1
                    } else {
                         newUnread[uid] = 0L // Sender has seen it
                    }
                }
                
                // 4. Update Conversation
                transaction.update(conversationRef, mapOf(
                    "lastMessage" to message.content,
                    "lastMessageSenderId" to message.senderId,
                    "lastMessageTimestamp" to com.google.firebase.Timestamp.now(),
                    "unreadCounts" to newUnread,
                    "seenBy" to listOf(message.senderId) // Only sender has seen it initially
                ))
            }.await()
            
            android.util.Log.d("ChatRepo", "Message sent successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ChatRepo", "Error sending message", e)
            Result.failure(e)
        }
    }

    override fun getConversations(userId: String): Flow<List<com.example.synoptrack.social.data.model.ConversationEntity>> {
        return kotlinx.coroutines.flow.callbackFlow {
            val query = firestore.collection("conversations")
                .whereArrayContains("participants", userId)
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)

            val registration = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val conversations = snapshot.toObjects(com.example.synoptrack.social.data.model.ConversationEntity::class.java)
                    trySend(conversations)
                }
            }
            awaitClose { registration.remove() }
        }
    }

    override suspend fun startConversation(targetUserId: String): Result<String> {
         return try {
             val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return Result.failure(Exception("No user"))
             val chatId = getConversationId(targetUserId) // Helper to get ID
             
             val docRef = firestore.collection("conversations").document(chatId)
             val snapshot = docRef.get().await()
             
             if (!snapshot.exists()) {
                 // Fetch user data for "participantData"
                 val userRef = firestore.collection("users").document(currentUserId)
                 val targetRef = firestore.collection("users").document(targetUserId)
                 
                 // Ideally use runBatch but simple gets are fine
                 val userProfile = userRef.get().await().toObject(com.example.synoptrack.profile.domain.model.UserProfile::class.java)
                 val targetProfile = targetRef.get().await().toObject(com.example.synoptrack.profile.domain.model.UserProfile::class.java)
                 
                 val participantData = mapOf(
                     currentUserId to com.example.synoptrack.social.data.model.ConversationParticipantData(userProfile?.displayName ?: "User", userProfile?.avatarUrl ?: ""),
                     targetUserId to com.example.synoptrack.social.data.model.ConversationParticipantData(targetProfile?.displayName ?: "User", targetProfile?.avatarUrl ?: "")
                 )
                 
                 val conversation = com.example.synoptrack.social.data.model.ConversationEntity(
                     id = chatId,
                     participants = listOf(currentUserId, targetUserId),
                     participantData = participantData,
                     lastMessageTimestamp = null // Empty start
                 )
                 docRef.set(conversation).await()
             }
             
             Result.success(chatId)
         } catch (e: Exception) {
             Result.failure(e)
         }
    }
    
    override suspend fun getConversationId(targetUserId: String): String {
        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        // Deterministic ID: sort uids
        val uids = listOf(currentUserId, targetUserId).sorted()
        val id = "${uids[0]}_${uids[1]}"
        android.util.Log.d("ChatRepo_Debug", "getConversationId: Generated $id (Me: $currentUserId, Target: $targetUserId)")
        return id
    }

    override fun getConversation(conversationId: String): Flow<com.example.synoptrack.social.data.model.ConversationEntity?> {
        return callbackFlow {
            android.util.Log.d("ChatRepo_Debug", "Listening to conversation: $conversationId")
            val docRef = firestore.collection("conversations").document(conversationId)
            val registration = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                     android.util.Log.e("ChatRepo_Debug", "Listen failed for conversation: $conversationId", error)
                     close(error)
                     return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    android.util.Log.d("ChatRepo_Debug", "Conversation update: ${snapshot.id} (Metadata: ${snapshot.metadata.isFromCache})")
                    trySend(snapshot.toObject(com.example.synoptrack.social.data.model.ConversationEntity::class.java))
                } else {
                    android.util.Log.w("ChatRepo_Debug", "Conversation document does not exist: $conversationId")
                    trySend(null)
                }
            }
            awaitClose { registration.remove() }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun markAsRead(conversationId: String, userId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("conversations").document(conversationId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists()) {
                    val currentUnread = snapshot.get("unreadCounts") as? Map<String, Long> ?: emptyMap()
                    val seenBy = snapshot.get("seenBy") as? List<String> ?: emptyList()
                    
                    // Only update if needed
                    val needUpdateUnread = (currentUnread[userId] ?: 0L) > 0
                    val needUpdateSeen = !seenBy.contains(userId)
                    
                    if (needUpdateUnread || needUpdateSeen) {
                         val newUnread = currentUnread.toMutableMap()
                         newUnread[userId] = 0
                         
                         val newSeen = seenBy.toMutableList()
                         if (!newSeen.contains(userId)) {
                             newSeen.add(userId)
                         }
                         
                         transaction.update(docRef, mapOf(
                             "unreadCounts" to newUnread,
                             "seenBy" to newSeen
                         ))
                    }
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
