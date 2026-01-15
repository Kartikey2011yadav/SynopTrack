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



sealed class AuthNavigationEvent {
    object NavigateToNameSetup : AuthNavigationEvent()
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
    
    // Phone Auth Removed
    
    fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            try {
                val account = task.result
                val idToken = account.idToken
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                authRepository.signInWithGoogle(credential)
                    .onSuccess { checkUserStatus() }
                    .onFailure { _signInState.value = SignInState.Error(it.message ?: "Sign in failed") }
            } catch (e: Exception) {
                _signInState.value = SignInState.Error(e.message ?: "Sign in failed")
            }
        }
    }
    

    // Phone Auth Methods Removed
    
    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            authRepository.signInWithEmail(email, pass)
                .onSuccess { checkUserStatus() }
                .onFailure { _signInState.value = SignInState.Error(it.message ?: "Sign in failed") }
        }
    }

    fun signUpWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            authRepository.signUpWithEmail(email, pass)
                .onSuccess { checkUserStatus() } // Or navigate to profile setup directly
                .onFailure { _signInState.value = SignInState.Error(it.message ?: "Sign up failed") }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            authRepository.resetPassword(email)
                .onSuccess { _signInState.value = SignInState.MessageSent("Reset link sent to $email") }
                .onFailure { _signInState.value = SignInState.Error(it.message ?: "Failed to send reset email") }
        }
    }
    
    fun saveIdentity(username: String, discriminator: String) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            val currentUser = authRepository.currentUser
            if (currentUser != null) {
                // Ideally trigger a backend check for uniqueness here.
                // For now, we update the profile locally/firestore.
                val uid = currentUser.uid
                // We need to fetch current profile or create a default one
                profileRepository.getUserProfile(uid).collect { profile ->
                    if (profile != null) {
                         val newInviteCode = com.example.synoptrack.core.utils.IdentityUtils.generateInviteCode(username, discriminator)
                         val updatedProfile = profile.copy(
                             username = username,
                             discriminator = discriminator,
                             inviteCode = newInviteCode
                         )
                         profileRepository.saveUserProfile(updatedProfile)
                             .onSuccess { 
                                 _navigationEvent.send(AuthNavigationEvent.NavigateToCompleteProfile)
                                 _signInState.value = SignInState.Success("Identity Saved")
                             }
                             .onFailure { _signInState.value = SignInState.Error("Failed to save identity") }
                    }
                }
            }
        }
    }

    private suspend fun checkUserStatus() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _signInState.value = SignInState.Error("User ID null")
            return
        }
        
        // We use ProfileRepository to get detailed status (username existence)
        profileRepository.getUserProfile(uid).collect { profile ->
             if (profile == null) {
                 // No profile data at all -> Start with Name Setup
                  _navigationEvent.send(AuthNavigationEvent.NavigateToNameSetup)
             } else {
                 if (profile.username.isEmpty()) {
                     _navigationEvent.send(AuthNavigationEvent.NavigateToNameSetup)
                 } else if (profile.displayName.isEmpty()) {
                     _navigationEvent.send(AuthNavigationEvent.NavigateToCompleteProfile)
                 } else {
                     _navigationEvent.send(AuthNavigationEvent.NavigateToPermissionCheck)
                     _signInState.value = SignInState.Success("Welcome back!")
                 }
             }
        }
    }
}

sealed class SignInState {
    object Initial : SignInState()
    object Loading : SignInState()
    // object OtpSent : SignInState()
    data class MessageSent(val message: String) : SignInState() // For forgot password
    data class Success(val message: String) : SignInState()
    data class Error(val message: String) : SignInState()
}