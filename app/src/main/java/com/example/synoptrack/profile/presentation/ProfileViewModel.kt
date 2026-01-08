package com.example.synoptrack.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.core.datastore.AppTheme
import com.example.synoptrack.core.datastore.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val themePreferences: ThemePreferences,
    private val profileRepository: com.example.synoptrack.profile.domain.repository.ProfileRepository,
    private val authRepository: com.example.synoptrack.auth.domain.repository.AuthRepository
) : ViewModel() {

    private val _userProfile = kotlinx.coroutines.flow.MutableStateFlow<com.example.synoptrack.profile.domain.model.UserProfile?>(null)
    val userProfile: StateFlow<com.example.synoptrack.profile.domain.model.UserProfile?> = _userProfile.asStateFlow()

    val currentTheme: StateFlow<AppTheme> = themePreferences.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            profileRepository.getUserProfile(uid).collect { profile ->
                _userProfile.value = profile
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            themePreferences.setTheme(theme)
        }
    }
}
