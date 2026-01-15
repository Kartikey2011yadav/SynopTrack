package com.example.synoptrack.auth.data.repository

import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: com.google.firebase.firestore.FirebaseFirestore
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun signInWithGoogle(credential: AuthCredential): Result<Boolean> {
        return try {
            firebaseAuth.signInWithCredential(credential).await()
            val uid = firebaseAuth.currentUser?.uid ?: return Result.failure(Exception("No UID"))
            // We rely on ViewModel to check status after sign-in
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun verifyPhoneNumber(
        activity: android.app.Activity,
        phoneNumber: String,
        callbacks: com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = com.google.firebase.auth.PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun signInWithPhoneCredential(credential: AuthCredential): Result<Boolean> {
        return try {
            firebaseAuth.signInWithCredential(credential).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, pass: String): Result<Boolean> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, pass: String): Result<Boolean> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Boolean> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserStatus(uid: String): Result<com.example.synoptrack.auth.domain.repository.UserStatus> {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            if (!snapshot.exists()) {
                Result.success(com.example.synoptrack.auth.domain.repository.UserStatus.NEW)
            } else {
                val isComplete = snapshot.getBoolean("isComplete") ?: false
                if (isComplete) {
                    Result.success(com.example.synoptrack.auth.domain.repository.UserStatus.COMPLETE)
                } else {
                    Result.success(com.example.synoptrack.auth.domain.repository.UserStatus.INCOMPLETE)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}