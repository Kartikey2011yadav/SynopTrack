package com.example.synoptrack.social.domain.model

import com.google.firebase.Timestamp

data class FriendRequest(
    val id: String = "",
    val senderId: String = "",
    val senderDisplayName: String = "", // Denormalized for easy display
    val senderAvatarUrl: String = "",
    val receiverId: String = "",
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
    val timestamp: Timestamp = Timestamp.now()
)

enum class FriendRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
