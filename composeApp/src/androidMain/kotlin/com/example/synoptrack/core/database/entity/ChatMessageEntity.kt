package com.example.synoptrack.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val groupId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val type: String = "text" // "text", "image", "location"
)
