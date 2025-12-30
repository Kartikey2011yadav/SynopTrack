package com.example.synoptrack.profile.domain.model

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    val createdAt: Long = 0,
    val ghostMode: Boolean = false,
    val theme: String = "system" // "system", "dark", "light"
)

