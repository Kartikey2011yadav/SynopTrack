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
    object NavigateToCompleteProfile : AuthNavigationEvent()
    object NavigateToPermissionCheck : AuthNavigationEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Initial)
    val signInState: StateFlow<SignInState> = _signInState

    private val _navigationEvent = Channel<AuthNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()
    
    // Phone Auth
    private var verificationId: String? = null
    var phoneNumber: String = ""

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
    
    fun startPhoneAuth(activity: android.app.Activity, phone: String) {
        phoneNumber = phone
        _signInState.value = SignInState.Loading
        
        val callbacks = object : com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                viewModelScope.launch {
                    authRepository.signInWithPhoneCredential(credential)
                        .onSuccess { checkUserStatus() }
                        .onFailure { _signInState.value = SignInState.Error(it.message ?: "Verification failed") }
                }
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                _signInState.value = SignInState.Error(e.message ?: "Verification failed")
            }

            override fun onCodeSent(id: String, token: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken) {
                verificationId = id
                _signInState.value = SignInState.OtpSent
            }
        }
        
        authRepository.verifyPhoneNumber(activity, phone, callbacks)
    }
    
    fun verifyOtp(code: String) {
        val id = verificationId ?: return
        _signInState.value = SignInState.Loading
        val credential = com.google.firebase.auth.PhoneAuthProvider.getCredential(id, code)
        viewModelScope.launch {
            authRepository.signInWithPhoneCredential(credential)
                .onSuccess { checkUserStatus() }
                .onFailure { _signInState.value = SignInState.Error(it.message ?: "Invalid Code") }
        }
    }
    
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
    
    private suspend fun checkUserStatus() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _signInState.value = SignInState.Error("User ID null")
            return
        }
        
        authRepository.getUserStatus(uid)
            .onSuccess { status ->
                 when (status) {
                     com.example.synoptrack.auth.domain.repository.UserStatus.COMPLETE -> {
                         _navigationEvent.send(AuthNavigationEvent.NavigateToPermissionCheck)
                         _signInState.value = SignInState.Success("Welcome back!")
                     }
                     com.example.synoptrack.auth.domain.repository.UserStatus.INCOMPLETE,
                     com.example.synoptrack.auth.domain.repository.UserStatus.NEW -> {
                         _navigationEvent.send(AuthNavigationEvent.NavigateToCompleteProfile)
                         _signInState.value = SignInState.Success("Please complete profile")
                     }
                 }
            }
            .onFailure {
                _signInState.value = SignInState.Error("Failed to check status")
            }
    }
}

sealed class SignInState {
    object Initial : SignInState()
    object Loading : SignInState()
    object OtpSent : SignInState()
    data class MessageSent(val message: String) : SignInState() // For forgot password
    data class Success(val message: String) : SignInState()
    data class Error(val message: String) : SignInState()
}