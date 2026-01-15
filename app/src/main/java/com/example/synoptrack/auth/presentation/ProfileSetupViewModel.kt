package com.example.synoptrack.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupState())
    val uiState = _uiState.asStateFlow()

    private var currentUserUid: String? = null

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = authRepository.currentUser
        if (user == null) {
            _uiState.update { it.copy(isLoading = false, error = "User not found") }
            return
        }
        currentUserUid = user.uid
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = profileRepository.getUserProfileOnce(user.uid)
            result.onSuccess { profile ->
                // If profile missing in Firestore, fallback to Auth User
                val email = profile?.email?.takeIf { it.isNotBlank() } ?: user.email ?: ""
                val phone = profile?.phoneNumber?.takeIf { it.isNotBlank() } ?: user.phoneNumber ?: ""
                val name = profile?.displayName.orEmpty()
                val bio = profile?.bio.orEmpty()
                val dob = profile?.dob.orEmpty()
                
                // Check verification (Skipped for now as per simplified flow)
                // var isEmailVerified = profile?.isEmailVerified ?: user.isEmailVerified
                // val isPhoneVerified = profile?.isPhoneVerified ?: (user.phoneNumber != null && user.phoneNumber == phone)

                _uiState.update { 
                    it.copy(
                        displayName = name,
                        email = email,
                        phoneNumber = phone,
                        bio = bio,
                        dob = dob,
                        avatarUrl = profile?.avatarUrl,
                        isLoading = false
                    )
                }
                validate()
            }.onFailure {
               _uiState.update { it.copy(isLoading = false, error = it.message) } 
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

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone) }
        validate()
    }

    fun onDobChanged(dob: String) {
        _uiState.update { it.copy(dob = dob) }
        validate()
    }
    
    fun onBioChanged(bio: String) {
        _uiState.update { it.copy(bio = bio) }
        validate()
    }

    private fun validate() {
        _uiState.update {
            val hasName = it.displayName.isNotBlank()
            // val hasEmail = it.email.isNotBlank() // Email is auto-filled or optional in this scope if we trust Auth
            val hasBio = it.bio.isNotBlank() // Let's make Bio mandatory as per "collect all details"
            val hasDob = it.dob.length >= 8
            
            val valid = hasName && hasBio && hasDob
            it.copy(isValid = valid)
        }
    }

    fun submitProfile() {
        val uid = currentUserUid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val current = _uiState.value
            
            val updatedProfile = UserProfile(
                uid = uid,
                displayName = current.displayName,
                email = current.email,
                phoneNumber = current.phoneNumber,
                bio = current.bio,
                dob = current.dob,
                isEmailVerified = false, // Skipped
                isPhoneVerified = false, // Skipped
                isComplete = true,
                avatarUrl = current.avatarUrl ?: ""
            )
            
            profileRepository.saveUserProfile(updatedProfile)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isComplete = true) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, error = it.message) }
                }
        }
    }
}

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
