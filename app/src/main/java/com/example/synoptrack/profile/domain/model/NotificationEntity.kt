package com.example.synoptrack.profile.domain.model

import com.google.firebase.Timestamp

enum class NotificationType {
    FRIEND_REQUEST,
    FRIEND_ACCEPTED,
    GENERIC,
    FOLLOW // If implementing one-way follows later
}

data class NotificationEntity(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: NotificationType = NotificationType.GENERIC,
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val message: String = "",
    val isRead: Boolean = false,
    val actionData: String? = null, // e.g., target ID or link
    val timestamp: Timestamp = Timestamp.now()
)
