package com.example.synoptrack.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.synoptrack.core.navigation.Screen

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<String?>(null)
    val destination: StateFlow<String?> = _destination.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = authRepository.currentUser
            if (currentUser != null) {
                profileRepository.getUserProfile(currentUser.uid).collect { profile ->
                     if (profile == null) {
                          _destination.value = Screen.NameSetup.route
                     } else {
                         if (profile.username.isEmpty()) {
                             _destination.value = Screen.NameSetup.route
                         } else if (profile.displayName.isEmpty()) {
                             _destination.value = Screen.ProfileSetup.route
                         } else {
                             _destination.value = Screen.Home.route
                         }
                     }
                }

            } else {
                _destination.value = Screen.Welcome.route
            }
        }
    }
}

