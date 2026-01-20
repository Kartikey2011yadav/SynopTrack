package com.example.synoptrack.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.core.presentation.model.ToastVariant
import com.example.synoptrack.core.presentation.util.ToastService
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
    private val profileRepository: ProfileRepository,
    private val toastService: ToastService
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
                authRepository.signInWithGoogle(credential)
                    .onSuccess {
                        toastService.showToast("Signed in successfully!", ToastVariant.SUCCESS)
                        checkUserStatus() 
                    }
                    .onFailure {
                        val msg = it.message ?: "Sign in failed"
                        _signInState.value = SignInState.Error(msg)
                        toastService.showToast(msg, ToastVariant.ERROR)
                    }
            } catch (e: Exception) {
                val msg = e.message ?: "Sign in failed"
                _signInState.value = SignInState.Error(msg)
                toastService.showToast(msg, ToastVariant.ERROR)
            }
        }
    }
    
    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            authRepository.signInWithEmail(email, pass)
                .onSuccess {
                    toastService.showToast("Welcome back!", ToastVariant.SUCCESS)
                    checkUserStatus() 
                }
                .onFailure {
                    val msg = it.message ?: "Sign in failed"
                    _signInState.value = SignInState.Error(msg)
                    toastService.showToast(msg, ToastVariant.ERROR)
                }
        }
    }

    fun signUpWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            authRepository.signUpWithEmail(email, pass)
                .onSuccess { 
                    toastService.showToast("Account created successfully!", ToastVariant.SUCCESS)
                    checkUserStatus() 
                } 
                .onFailure {
                    val msg = it.message ?: "Sign up failed"
                    _signInState.value = SignInState.Error(msg)
                    toastService.showToast(msg, ToastVariant.ERROR)
                }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            authRepository.resetPassword(email)
                .onSuccess {
                    val msg = "Reset link sent to $email"
                    _signInState.value = SignInState.MessageSent(msg)
                    toastService.showToast(msg, ToastVariant.SUCCESS)
                }
                .onFailure {
                    val msg = it.message ?: "Failed to send reset email"
                    _signInState.value = SignInState.Error(msg)
                    toastService.showToast(msg, ToastVariant.ERROR)
                }
        }
    }
    
    fun saveIdentity(username: String, discriminator: String) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            
            // Check availability one last time
            val isAvailable = profileRepository.checkIdentityAvailability(username, discriminator).getOrDefault(false)
            if (!isAvailable) {
                 val msg = "This Username#Hash is already taken. Please choose another hash."
                 _signInState.value = SignInState.Error(msg)
                 toastService.showToast(msg, ToastVariant.WARNING)
                 return@launch
            }
            
            val currentUser = authRepository.currentUser
            if (currentUser != null) {
                val uid = currentUser.uid
                profileRepository.getUserProfileOnce(uid).onSuccess { profile ->
                     val newInviteCode = com.example.synoptrack.core.utils.IdentityUtils.generateInviteCode(username, discriminator)
                     
                     val updatedProfile = if (profile != null) {
                         profile.copy(
                             username = username,
                             discriminator = discriminator,
                             inviteCode = newInviteCode
                         )
                     } else {
                         com.example.synoptrack.profile.domain.model.UserProfile(
                             uid = uid,
                             email = currentUser.email ?: "",
                             displayName = username,
                             username = username,
                             discriminator = discriminator,
                             inviteCode = newInviteCode,
                             createdAt = java.util.Date()
                         )
                     }

                     profileRepository.saveUserProfile(updatedProfile)
                         .onSuccess { 
                             toastService.showToast("Identity Saved!", ToastVariant.SUCCESS)
                             _navigationEvent.send(AuthNavigationEvent.NavigateToCompleteProfile)
                             _signInState.value = SignInState.Success("Identity Saved")
                         }
                         .onFailure {
                             val msg = "Failed to save identity"
                             _signInState.value = SignInState.Error(msg)
                             toastService.showToast(msg, ToastVariant.ERROR)
                         }
                }.onFailure {
                    val msg = "Failed to fetch profile"
                    _signInState.value = SignInState.Error(msg)
                    toastService.showToast(msg, ToastVariant.ERROR)
                }
            }
        }
    }

    suspend fun checkIdentityAvailability(username: String, discriminator: String): Boolean {
        return profileRepository.checkIdentityAvailability(username, discriminator).getOrDefault(false)
    }

    private suspend fun checkUserStatus() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            val msg = "User ID null"
            _signInState.value = SignInState.Error(msg)
            toastService.showToast(msg, ToastVariant.ERROR)
            return
        }
        
        profileRepository.getUserProfile(uid).collect { profile ->
             if (profile == null) {
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
    data class MessageSent(val message: String) : SignInState()
    data class Success(val message: String) : SignInState()
    data class Error(val message: String) : SignInState()
}