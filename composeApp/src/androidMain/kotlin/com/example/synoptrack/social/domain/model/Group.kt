package com.example.synoptrack.social.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Group(
    val id: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val ownerId: String = "",
    val memberIds: List<String> = emptyList(),
    @ServerTimestamp
    val createdAt: Date? = null
)
