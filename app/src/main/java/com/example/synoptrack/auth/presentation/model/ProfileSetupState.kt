package com.example.synoptrack.auth.presentation.model

data class ProfileSetupState(
    val displayName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val dob: String = "",
    val avatarUrl: String? = null,
    
    val showEmailField: Boolean = true, 
    
    val isLoading: Boolean = false,
    val isValid: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null,
    val message: String? = null
)
