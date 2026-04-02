package com.example.synoptrack.social.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ConversationEntity(
    val id: String = "", // uid1_uid2 (sorted alphabetically)
    val participants: List<String> = emptyList(), // [uid1, uid2]
    val lastMessage: String = "",
    val lastMessageSenderId: String = "",
    @ServerTimestamp
    val lastMessageTimestamp: Date? = null,
    val participantData: Map<String, ConversationParticipantData> = emptyMap(),
    
    // New Fields for UI Features
    val unreadCounts: Map<String, Int> = emptyMap(), // UserId -> Count
    val seenBy: List<String> = emptyList(), // UserIds who saw the last message
    
    // Logic for vanish mode could go here (e.g. "expiresAt")
    val type: String = "private" // "private" or "group"
)

data class ConversationParticipantData(
    val displayName: String = "",
    val avatarUrl: String = ""
)
