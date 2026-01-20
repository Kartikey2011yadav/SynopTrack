package com.example.synoptrack.social.domain.model

data class UserGroup(
    val id: String = "",
    val name: String = "",
    val memberIds: List<String> = emptyList()
)
