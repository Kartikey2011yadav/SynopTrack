package com.example.synoptrack.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupState())
    val uiState = _uiState.asStateFlow()

    init {
        checkExistingData()
    }

    private fun checkExistingData() {
        val user = authRepository.currentUser
        if (user != null) {
            _uiState.update { 
                it.copy(
                    email = user.email ?: "",
                    showEmailField = user.email.isNullOrBlank()
                )
            }
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(displayName = name) }
        validate()
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
        validate()
    }

    fun onDobChanged(dob: String) {
        _uiState.update { it.copy(dob = dob) }
        validate()
    }

    private fun validate() {
        _uiState.update {
            it.copy(isValid = it.displayName.isNotBlank() && it.dob.length >= 8 && it.email.contains("@"))
        }
    }

    fun submitProfile() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            profileRepository.createOrUpdateUser(
                uid = uid,
                email = _uiState.value.email,
                displayName = _uiState.value.displayName,
                photoUrl = null
            )
            // Save DOB separately or update repo to handle it
            // For now assuming createOrUpdateUser handles basic fields or we extend it
            
            _uiState.update { it.copy(isLoading = false, isComplete = true) }
        }
    }
}

data class ProfileSetupState(
    val displayName: String = "",
    val email: String = "",
    val dob: String = "",
    val showEmailField: Boolean = false,
    val isLoading: Boolean = false,
    val isValid: Boolean = false,
    val isComplete: Boolean = false
)
