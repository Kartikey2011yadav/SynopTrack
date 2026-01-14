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
                // Check if profile exists and is complete
                authRepository.getUserStatus(currentUser.uid)
                    .onSuccess { status ->
                        when (status) {
                            com.example.synoptrack.auth.domain.repository.UserStatus.COMPLETE -> {
                                _destination.value = Screen.Home.route
                            }
                            else -> {
                                // New or Incomplete
                                _destination.value = Screen.ProfileSetup.route
                            }
                        }
                    }
                    .onFailure {
                        // Fallback to Welcome on error
                        _destination.value = Screen.Welcome.route
                    }
            } else {
                _destination.value = Screen.Welcome.route
            }
        }
    }
}

