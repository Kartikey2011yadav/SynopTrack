package com.example.synoptrack.domain.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signInWithGoogle(credential: AuthCredential): Result<Boolean>
    fun signOut()
}