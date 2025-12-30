package com.example.synoptrack.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NameSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isComplete = MutableStateFlow(false)
    val isComplete: StateFlow<Boolean> = _isComplete.asStateFlow()

    fun saveName(displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUser = authRepository.currentUser
            if (currentUser != null) {
                val result = profileRepository.createOrUpdateUser(
                    uid = currentUser.uid,
                    email = currentUser.email ?: "",
                    displayName = displayName,
                    photoUrl = currentUser.photoUrl?.toString()
                )
                if (result.isSuccess) {
                    _isComplete.value = true
                }
            }
            _isLoading.value = false
        }
    }
}

