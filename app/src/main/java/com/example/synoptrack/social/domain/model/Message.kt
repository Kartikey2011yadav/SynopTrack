package com.example.synoptrack.social.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "", // Denormalized for easy display
    val content: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    val type: String = "text" // "text", "image", "location"
)
