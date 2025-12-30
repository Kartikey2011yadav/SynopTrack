package com.example.synoptrack.mapos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapOSViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapOSUiState>(MapOSUiState())
    val uiState: StateFlow<MapOSUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val currentUser = authRepository.currentUser
            if (currentUser != null) {
                profileRepository.getUserProfile(currentUser.uid).collect { profile ->
                    _uiState.update { it.copy(userProfile = profile) }
                }
            }
        }
    }

    fun toggleChat() {
        _uiState.update { it.copy(showChat = !it.showChat) }
    }

    fun toggleProfile() {
        _uiState.update { it.copy(showProfile = !it.showProfile) }
    }

    fun toggleMoments() {
        _uiState.update { it.copy(showMoments = !it.showMoments) }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(darkMode = !it.darkMode) }
        // Also update in Firestore if needed, or just local state for now
        // If we want to persist theme:
        viewModelScope.launch {
            val profile = _uiState.value.userProfile
            if (profile != null) {
                val newTheme = if (_uiState.value.darkMode) "dark" else "light"
                profileRepository.updateTheme(profile.uid, newTheme)
            }
        }
    }

    fun toggleGhostMode(isEnabled: Boolean) {
        viewModelScope.launch {
            val profile = _uiState.value.userProfile
            if (profile != null) {
                profileRepository.updateGhostMode(profile.uid, isEnabled)
            }
        }
    }

    fun logout() {
        authRepository.signOut()
        // Navigation back to login should be handled by the UI observing auth state or a one-time event
    }

    fun closeAllSheets() {
        _uiState.update {
            it.copy(
                showChat = false,
                showProfile = false,
                showMoments = false
            )
        }
    }
}
