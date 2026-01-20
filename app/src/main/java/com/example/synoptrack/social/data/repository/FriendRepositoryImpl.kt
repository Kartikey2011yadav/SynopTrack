package com.example.synoptrack.social.data.repository

import com.example.synoptrack.profile.domain.model.NotificationEntity
import com.example.synoptrack.profile.domain.model.NotificationType
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.social.domain.model.FriendRequest
import com.example.synoptrack.social.domain.model.FriendRequestStatus
import com.example.synoptrack.social.domain.repository.FriendRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FriendRepository {

    private val usersCollection = firestore.collection("users")
    private val requestsCollection = firestore.collection("friend_requests")

    override suspend fun sendFriendRequest(currentUserId: String, targetUserId: String): Result<Boolean> {
        return try {
            firestore.runTransaction { transaction ->
                // 1. Create Request Document
                val senderDoc = transaction.get(usersCollection.document(currentUserId))
                val senderProfile = senderDoc.toObject(UserProfile::class.java) 
                    ?: throw Exception("Sender not found")
                
                // Check if already friends or requested
                if (senderProfile.friends.contains(targetUserId)) throw Exception("Already friends")
                if (senderProfile.sentRequests.contains(targetUserId)) throw Exception("Request already sent")

                val requestId = UUID.randomUUID().toString()
                val request = FriendRequest(
                    id = requestId,
                    senderId = currentUserId,
                    senderDisplayName = "${senderProfile.username}#${senderProfile.discriminator}",
                    senderAvatarUrl = senderProfile.avatarUrl,
                    receiverId = targetUserId,
                    status = FriendRequestStatus.PENDING,
                    timestamp = Timestamp.now()
                )
                
                transaction.set(requestsCollection.document(requestId), request)

                // 2. Update Sender's sentRequests
                transaction.update(usersCollection.document(currentUserId), "sentRequests", FieldValue.arrayUnion(targetUserId))

                // 3. Update Receiver's receivedRequests AND add Notification
                val notification = NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    type = NotificationType.FRIEND_REQUEST,
                    senderId = currentUserId,
                    senderName = request.senderDisplayName,
                    senderAvatar = request.senderAvatarUrl,
                    message = "sent you a friend request.",
                    actionData = requestId,
                    timestamp = Timestamp.now()
                )
                
                transaction.update(usersCollection.document(targetUserId), 
                    mapOf(
                        "receivedRequests" to FieldValue.arrayUnion(currentUserId),
                        "notifications" to FieldValue.arrayUnion(notification)
                    )
                )
            }.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptFriendRequest(requestId: String): Result<Boolean> {
        return try {
            val requestSnap = requestsCollection.document(requestId).get().await()
            val request = requestSnap.toObject(FriendRequest::class.java) 
                ?: return Result.failure(Exception("Request not found"))

            firestore.runTransaction { transaction ->
                // 1. Update Request Status
                transaction.update(requestsCollection.document(requestId), "status", FriendRequestStatus.ACCEPTED)

                // 2. Add to Friends List & Remove from Requests for BOTH columns
                val senderRef = usersCollection.document(request.senderId)
                val receiverRef = usersCollection.document(request.receiverId)

                // Update Sender: Add Friend, Remove Sent Request, Add Notification (Accepted)
                val receiverSnap = transaction.get(receiverRef)
                val receiverProfile = receiverSnap.toObject(UserProfile::class.java)
                val receiverName = receiverProfile?.username ?: "User"

                val notification = NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    type = NotificationType.FRIEND_ACCEPTED,
                    senderId = request.receiverId,
                    senderName = receiverName,
                    senderAvatar = receiverProfile?.avatarUrl ?: "",
                    message = "accepted your friend request.",
                    timestamp = Timestamp.now()
                )

                transaction.update(senderRef, 
                    mapOf(
                        "friends" to FieldValue.arrayUnion(request.receiverId),
                        "sentRequests" to FieldValue.arrayRemove(request.receiverId),
                        "notifications" to FieldValue.arrayUnion(notification)
                    )
                )

                // Update Receiver: Add Friend, Remove Received Request
                transaction.update(receiverRef,
                    mapOf(
                        "friends" to FieldValue.arrayUnion(request.senderId),
                        "receivedRequests" to FieldValue.arrayRemove(request.senderId)
                        // Notification for receiver is redundant as they clicked accept
                    )
                )
            }.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectFriendRequest(requestId: String): Result<Boolean> {
        return try {
            val requestSnap = requestsCollection.document(requestId).get().await()
            val request = requestSnap.toObject(FriendRequest::class.java) ?: return Result.failure(Exception("Request not found"))
            
            firestore.runTransaction { transaction ->
                transaction.update(requestsCollection.document(requestId), "status", FriendRequestStatus.REJECTED)
                transaction.update(usersCollection.document(request.receiverId), "receivedRequests", FieldValue.arrayRemove(request.senderId))
                transaction.update(usersCollection.document(request.senderId), "sentRequests", FieldValue.arrayRemove(request.receiverId))
            }.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPendingRequests(userId: String): Flow<List<FriendRequest>> = callbackFlow {
        val listener = requestsCollection
            .whereEqualTo("receiverId", userId)
            .whereEqualTo("status", FriendRequestStatus.PENDING.name)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val requests = snapshot?.toObjects(FriendRequest::class.java) ?: emptyList()
                trySend(requests)
            }
        awaitClose { listener.remove() }
    }
    
    // NEW: Get Users Notifications
    override fun getNotifications(userId: String): Flow<List<NotificationEntity>> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val profile = snapshot?.toObject(UserProfile::class.java)
                val notifications = profile?.notifications?.sortedByDescending { it.timestamp } ?: emptyList()
                trySend(notifications)
            }
        awaitClose { listener.remove() }
    }

    override fun getFriends(userId: String): Flow<List<UserProfile>> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val profile = snapshot?.toObject(UserProfile::class.java)
                val friendIds = profile?.friends ?: emptyList()
                
                if (friendIds.isEmpty()) {
                    trySend(emptyList())
                } else {
                    // Chunked query for friends
                    // For prototype, limiting to first 10. In prod, need batching.
                    usersCollection.whereIn("uid", friendIds.take(10)).get()
                        .addOnSuccessListener { friendsSnap ->
                             val friends = friendsSnap.toObjects(UserProfile::class.java)
                             trySend(friends)
                        }
                        .addOnFailureListener {
                            // On failure (e.g. too many clauses), just send empty or cached
                        }
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun searchUsers(nameQuery: String, discriminatorQuery: String): Result<List<UserProfile>> {
        return try {
             if (discriminatorQuery.isNotEmpty()) {
                val snapshot = usersCollection
                    .whereEqualTo("username", nameQuery)
                    .whereEqualTo("discriminator", discriminatorQuery)
                    .get().await()
                 return Result.success(snapshot.toObjects(UserProfile::class.java))
            } 
            
            if (nameQuery.contains("@")) {
                 val codeSnapshot = usersCollection.whereEqualTo("inviteCode", nameQuery).get().await()
                 if (!codeSnapshot.isEmpty) {
                     return Result.success(codeSnapshot.toObjects(UserProfile::class.java))
                 }
            }

            val nameSnapshot = usersCollection
                 .whereGreaterThanOrEqualTo("username", nameQuery)
                 .whereLessThanOrEqualTo("username", nameQuery + "\uf8ff")
                 .limit(10)
                 .get().await()
                 
            Result.success(nameSnapshot.toObjects(UserProfile::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserByInviteCode(code: String): Result<UserProfile?> {
        return try {
             val snapshot = usersCollection.whereEqualTo("inviteCode", code).get().await()
             if (snapshot.isEmpty) Result.success(null)
             else Result.success(snapshot.documents[0].toObject(UserProfile::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
