package com.example.synoptrack.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.domain.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignInState {
    object Loading : SignInState()
    data class Success(val message: String) : SignInState()
    data class Error(val message: String) : SignInState()
    object Initial : SignInState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Initial)
    val signInState: StateFlow<SignInState> = _signInState

    fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            try {
                val account = task.result
                val idToken = account.idToken
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                authRepository.signInWithGoogle(credential).fold(
                    onSuccess = { _signInState.value = SignInState.Success("Sign-in successful") },
                    onFailure = { _signInState.value = SignInState.Error(it.message ?: "An unknown error occurred") }
                )
            } catch (e: Exception) {
                _signInState.value = SignInState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}