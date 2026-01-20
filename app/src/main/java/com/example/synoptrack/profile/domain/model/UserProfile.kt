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
    @get:com.google.firebase.firestore.PropertyName("private")
    @set:com.google.firebase.firestore.PropertyName("private")
    var isPrivate: Boolean = false,
    val bio: String = "",
    val dob: String = "", // DD/MM/YYYY
    
    // Onboarding Status
    @get:com.google.firebase.firestore.PropertyName("complete")
    @set:com.google.firebase.firestore.PropertyName("complete")
    var isComplete: Boolean = false,

    @get:com.google.firebase.firestore.PropertyName("emailVerified")
    @set:com.google.firebase.firestore.PropertyName("emailVerified")
    var isEmailVerified: Boolean = false,

    @get:com.google.firebase.firestore.PropertyName("phoneVerified")
    @set:com.google.firebase.firestore.PropertyName("phoneVerified")
    var isPhoneVerified: Boolean = false,

    // New fields for Premium/Social features
    val fcmToken: String = "",
    val batteryLevel: Int = -1,

    @get:com.google.firebase.firestore.PropertyName("charging")
    @set:com.google.firebase.firestore.PropertyName("charging")
    var isCharging: Boolean = false,

    val phoneNumber: String? = null,
    // Identity
    val username: String = "", 
    val discriminator: String = "", // The 4-char hash (e.g., "9uwu")
    val inviteCode: String = "", // Unique Code
    @ServerTimestamp
    val lastActiveAt: Date? = null,
    
    // Social Graph
    val friends: List<String> = emptyList(), // List of accepted friend UIDs
    val receivedRequests: List<String> = emptyList(), // List of UIDs who sent requests
    val sentRequests: List<String> = emptyList(), // List of UIDs requested by this user
    
    // Notifications (Embedded for simplicity as per requirements)
    val notifications: List<NotificationEntity> = emptyList()
)