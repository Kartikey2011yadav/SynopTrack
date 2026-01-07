package com.example.synoptrack.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionUiState())
    val uiState: StateFlow<PermissionUiState> = _uiState.asStateFlow()

    fun onPermissionGranted() {
        _uiState.value = _uiState.value.copy(isPermissionGranted = true)
        updateGhostMode(false)
    }

    fun onPermissionDenied(shouldShowRationale: Boolean) {
        if (shouldShowRationale) {
            _uiState.value = _uiState.value.copy(
                showRationale = true,
                errorMessage = "We need location to show your friends. Please try again."
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isPermanentlyDenied = true,
                showRationale = false
            )
        }
    }

    fun onSkipForNow() {
        updateGhostMode(true)
        _uiState.value = _uiState.value.copy(isSkipped = true)
    }

    fun resetRationale() {
        _uiState.value = _uiState.value.copy(showRationale = false, errorMessage = null)
    }

    private fun updateGhostMode(isGhost: Boolean) {
        viewModelScope.launch {
            val user = authRepository.currentUser
            if (user != null) {
                profileRepository.updateGhostMode(user.uid, isGhost)
            }
        }
    }
}

data class PermissionUiState(
    val isPermissionGranted: Boolean = false,
    val isPermanentlyDenied: Boolean = false,
    val isSkipped: Boolean = false,
    val showRationale: Boolean = false,
    val errorMessage: String? = null
)
