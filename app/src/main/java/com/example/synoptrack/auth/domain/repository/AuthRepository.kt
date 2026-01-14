package com.example.synoptrack.auth.domain.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signInWithGoogle(credential: AuthCredential): Result<Boolean>
    
    // Phone Auth
    fun verifyPhoneNumber(
        activity: android.app.Activity, 
        phoneNumber: String,
        callbacks: com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )
    suspend fun signInWithPhoneCredential(credential: AuthCredential): Result<Boolean>

    // Email Auth
    suspend fun signInWithEmail(email: String, pass: String): Result<Boolean>
    suspend fun signUpWithEmail(email: String, pass: String): Result<Boolean>
    suspend fun resetPassword(email: String): Result<Boolean>
    
    // Status Check
    suspend fun getUserStatus(uid: String): Result<UserStatus>
    
    fun signOut()
}

enum class UserStatus {
    NEW, INCOMPLETE, COMPLETE
}