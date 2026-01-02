package com.example.synoptrack.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignInState {
    object Loading : SignInState()
    data class Success(val message: String) : SignInState()
    data class Error(val message: String) : SignInState()
    object Initial : SignInState()
}

sealed class AuthNavigationEvent {
    object NavigateToCompleteProfile : AuthNavigationEvent()
    object NavigateToPermissionCheck : AuthNavigationEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Initial)
    val signInState: StateFlow<SignInState> = _signInState

    private val _navigationEvent = Channel<AuthNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            try {
                val account = task.result
                val idToken = account.idToken
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                authRepository.signInWithGoogle(credential).fold(
                    onSuccess = {
                        val user = authRepository.currentUser
                        if (user != null) {
                            // Check if user profile exists and is complete
                            val result = profileRepository.getUserProfileOnce(user.uid)
                            result.fold(
                                onSuccess = { profile ->
                                    if (profile == null || profile.displayName.isBlank()) {
                                        // New user or incomplete profile
                                        // We might want to create the basic user here or let the next screen do it
                                        // For now, we'll just navigate
                                        _navigationEvent.send(AuthNavigationEvent.NavigateToCompleteProfile)
                                    } else {
                                        // User exists and has a name
                                        _navigationEvent.send(AuthNavigationEvent.NavigateToPermissionCheck)
                                    }
                                    _signInState.value = SignInState.Success("Sign-in successful")
                                },
                                onFailure = {
                                    _signInState.value = SignInState.Error("Failed to fetch profile: ${it.message}")
                                }
                            )
                        } else {
                            _signInState.value = SignInState.Error("User not found after sign in")
                        }
                    },
                    onFailure = { _signInState.value = SignInState.Error(it.message ?: "An unknown error occurred") }
                )
            } catch (e: Exception) {
                _signInState.value = SignInState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}