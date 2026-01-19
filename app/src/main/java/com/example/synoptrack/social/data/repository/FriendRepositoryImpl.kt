package com.example.synoptrack.social.data.repository

import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.social.domain.model.FriendRequest
import com.example.synoptrack.social.domain.model.FriendRequestStatus
import com.example.synoptrack.social.domain.repository.FriendRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FriendRepository {

    private val usersCollection = firestore.collection("users")
    private val requestsCollection = firestore.collection("friend_requests")

    override suspend fun sendFriendRequest(currentUserId: String, targetUserId: String): Result<Boolean> {
        return try {
            // Check if request already exists
            val existing = requestsCollection
                .whereEqualTo("senderId", currentUserId)
                .whereEqualTo("receiverId", targetUserId)
                .whereEqualTo("status", FriendRequestStatus.PENDING.name)
                .get().await()

            if (!existing.isEmpty) {
                return Result.failure(Exception("Request already pending"))
            }

            // Get sender profile for denormalization
            val senderProfile = usersCollection.document(currentUserId).get().await().toObject(UserProfile::class.java)
                ?: return Result.failure(Exception("Sender profile not found"))

            val request = FriendRequest(
                id = requestsCollection.document().id,
                senderId = currentUserId,
                senderDisplayName = "${senderProfile.username}#${senderProfile.discriminator}", // Use identity format
                senderAvatarUrl = senderProfile.avatarUrl,
                receiverId = targetUserId,
                status = FriendRequestStatus.PENDING
            )

            requestsCollection.document(request.id).set(request).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptFriendRequest(requestId: String): Result<Boolean> {
        return try {
            val requestDoc = requestsCollection.document(requestId).get().await()
            val request = requestDoc.toObject(FriendRequest::class.java)
                ?: return Result.failure(Exception("Request not found"))

            firestore.runTransaction { transaction ->
                // Update request status
                transaction.update(requestsCollection.document(requestId), "status", FriendRequestStatus.ACCEPTED)

                // Add to each other's friend lists (subcollections or array)
                // Using 'friends' subcollection for scalability
                val senderFriendRef = usersCollection.document(request.senderId).collection("friends").document(request.receiverId)
                val receiverFriendRef = usersCollection.document(request.receiverId).collection("friends").document(request.senderId)
                
                // We just store a placeholder timestamp or partial friend object
                val friendData = mapOf("since" to com.google.firebase.Timestamp.now())
                
                transaction.set(senderFriendRef, friendData)
                transaction.set(receiverFriendRef, friendData)
            }.await()
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectFriendRequest(requestId: String): Result<Boolean> {
         return try {
            requestsCollection.document(requestId).update("status", FriendRequestStatus.REJECTED).await()
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

    override fun getFriends(userId: String): Flow<List<UserProfile>> = callbackFlow {
        // This is complex with Firestore. 
        // 1. Listen to 'friends' subcollection IDs.
        // 2. Query 'users' collection with 'in' clause (chunked by 10).
        // For simplicity now, we'll verify connection via separate mechanism or implement simpler logic later.
        // Or: Store basic friend info in the subcollection so we don't need a second query immediately.
        
        // Let's implement a basic version that fetches IDs
        val listener = usersCollection.document(userId).collection("friends")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val friendIds = snapshot?.documents?.map { it.id } ?: emptyList()
                if (friendIds.isEmpty()) {
                    trySend(emptyList())
                } else {
                    // Fetch profiles
                    // Note: 'in' queries limited to 10. For production, execute multiple queries.
                    // For prototype, we fetch one by one or take top 10.
                    // Let's just do a naive fetch for now suitable for prototype.
                     usersCollection.whereIn("uid", friendIds.take(10)).get()
                         .addOnSuccessListener { profileSnap ->
                             val profiles = profileSnap.toObjects(UserProfile::class.java)
                             trySend(profiles)
                         }
                }
            }
         awaitClose { listener.remove() }
    }

    override suspend fun searchUsers(nameQuery: String, discriminatorQuery: String): Result<List<UserProfile>> {
        return try {
            if (discriminatorQuery.isNotEmpty()) {
                // Exact Match: Username + Discriminator
                val snapshot = usersCollection
                    .whereEqualTo("username", nameQuery)
                    .whereEqualTo("discriminator", discriminatorQuery)
                    .get().await()
                 return Result.success(snapshot.toObjects(UserProfile::class.java))
            } 
            
            // Search by Invite Code (Check if query matches Invite Code format or just try finding it)
            // New Format: name#tag@random (contains @)
            if (nameQuery.contains("@")) {
                 val codeSnapshot = usersCollection.whereEqualTo("inviteCode", nameQuery).get().await()
                 if (!codeSnapshot.isEmpty) {
                     return Result.success(codeSnapshot.toObjects(UserProfile::class.java))
                 }
            }

            // Username Prefix Search
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
