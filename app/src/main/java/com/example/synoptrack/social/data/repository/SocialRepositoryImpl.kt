package com.example.synoptrack.social.data.repository

import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.social.domain.model.Group
import com.example.synoptrack.social.domain.repository.SocialRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class SocialRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : SocialRepository {

    override suspend fun createGroup(name: String, ownerId: String): Result<String> {
        return try {
            val groupId = UUID.randomUUID().toString()
            val inviteCode = generateInviteCode()
            val group = Group(
                id = groupId,
                name = name,
                inviteCode = inviteCode,
                ownerId = ownerId,
                memberIds = listOf(ownerId)
            )
            firestore.collection("groups").document(groupId).set(group).await()
            
            // Add group to user's group list (optional, or just query by memberIds)
            firestore.collection("users").document(ownerId)
                .update("groupIds", FieldValue.arrayUnion(groupId)) // Assuming we add this field to UserProfile later or subcollection
                .await() // Ideally should be a transaction
                
            Result.success(groupId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinGroup(inviteCode: String, userId: String): Result<Unit> {
        return try {
            val query = firestore.collection("groups")
                .whereEqualTo("inviteCode", inviteCode)
                .limit(1)
                .get()
                .await()

            if (query.isEmpty) {
                return Result.failure(Exception("Invalid invite code"))
            }

            val groupDoc = query.documents.first()
            val groupId = groupDoc.id

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(groupDoc.reference)
                val currentMembers = snapshot.get("memberIds") as? List<String> ?: emptyList()
                if (!currentMembers.contains(userId)) {
                    transaction.update(groupDoc.reference, "memberIds", FieldValue.arrayUnion(userId))
                }
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun leaveGroup(groupId: String, userId: String): Result<Unit> {
         return try {
            firestore.collection("groups").document(groupId)
                .update("memberIds", FieldValue.arrayRemove(userId))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getGroupMembers(groupId: String): Flow<List<UserProfile>> = callbackFlow {
        // This monitors the GROUP document to get the member IDs, then fetches profiles
        // A better approach for scalability might be a subcollection 'members', but array is fine for small groups
        val listener = firestore.collection("groups").document(groupId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val memberIds = snapshot.get("memberIds") as? List<String> ?: emptyList()
                    if (memberIds.isNotEmpty()) {
                        // Fetch profiles. Note: 'in' query limited to 10-30 items depending on efficiency.
                        // For larger groups, we'd need to fetch individually or use a different structure.
                        firestore.collection("users")
                            .whereIn("uid", memberIds)
                            .get()
                            .addOnSuccessListener { result ->
                                val profiles = result.toObjects(UserProfile::class.java)
                                trySend(profiles)
                            }
                    } else {
                        trySend(emptyList())
                    }
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getUserGroups(userId: String): Flow<List<Group>> = callbackFlow {
        val listener = firestore.collection("groups")
            .whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val groups = snapshot.toObjects(Group::class.java)
                    trySend(groups)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addFriend(inviteCode: String, userId: String): Result<Unit> {
        return try {
            // 1. Find user by invite code
            val query = firestore.collection("users")
                .whereEqualTo("inviteCode", inviteCode)
                .limit(1)
                .get()
                .await()

            if (query.isEmpty) {
                return Result.failure(Exception("Invalid invite code"))
            }

            val friendDoc = query.documents.first()
            val friendId = friendDoc.id

            if (friendId == userId) {
                return Result.failure(Exception("You cannot add yourself"))
            }

            // 2. Add to mutual friends subcollection
            val batch = firestore.batch()
            
            // Add Friend to My List
            val myFriendRef = firestore.collection("users").document(userId)
                .collection("friends").document(friendId)
            batch.set(myFriendRef, mapOf("uid" to friendId, "addedAt" to FieldValue.serverTimestamp()))

            // Add Me to Friend's List
            val friendFriendRef = firestore.collection("users").document(friendId)
                .collection("friends").document(userId)
            batch.set(friendFriendRef, mapOf("uid" to userId, "addedAt" to FieldValue.serverTimestamp()))

            batch.commit().await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFriends(userId: String): Flow<List<UserProfile>> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .collection("friends")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val friendIds = snapshot.documents.mapNotNull { it.getString("uid") }
                    if (friendIds.isNotEmpty()) {
                        // Batch fetch profiles (limit 10 per 'in' query usually, need chunking for production)
                        // Ideally strictly cap friends or use pagination. For now, simple 'whereIn'.
                        firestore.collection("users")
                            .whereIn("uid", friendIds.take(10)) 
                            .get()
                            .addOnSuccessListener { result ->
                                val friends = result.toObjects(UserProfile::class.java)
                                trySend(friends)
                            }
                    } else {
                        trySend(emptyList())
                    }
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }


    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }
}
