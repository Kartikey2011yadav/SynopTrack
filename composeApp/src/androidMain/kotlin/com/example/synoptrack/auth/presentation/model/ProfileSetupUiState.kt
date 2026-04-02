package com.example.synoptrack.auth.presentation.model

data class ProfileSetupUiState(
    val displayName: String = "",
    val bio: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val dob: String = "",
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val isValid: Boolean = false,
    val error: String? = null,
    val message: String? = null
)
