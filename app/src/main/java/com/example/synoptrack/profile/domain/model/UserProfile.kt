package com.example.synoptrack.profile.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    val ghostMode: Boolean = false,
    val theme: String = "system", // "system", "dark", "light"
    val lastLocation: Any? = null, // GeoPoint or Map
    
    // Privacy & Bio
    val isPrivate: Boolean = false,
    val bio: String = "",

    // New fields for Premium/Social features
    val fcmToken: String = "",
    val batteryLevel: Int = -1,
    val isCharging: Boolean = false,
    val phoneNumber: String? = null,
    val inviteCode: String = "", // Unique 6-char code
    @ServerTimestamp
    val lastActiveAt: Date? = null
)