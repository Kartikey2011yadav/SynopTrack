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
    val dob: String = "", // DD/MM/YYYY
    
    // Onboarding Status
    val isComplete: Boolean = false,
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,

    // New fields for Premium/Social features
    val fcmToken: String = "",
    val batteryLevel: Int = -1,
    val isCharging: Boolean = false,
    val phoneNumber: String? = null,
    // Identity
    val username: String = "", 
    val discriminator: String = "", // The 4-char hash (e.g., "9uwu")
    val inviteCode: String = "", // Unique Code
    @ServerTimestamp
    val lastActiveAt: Date? = null
)