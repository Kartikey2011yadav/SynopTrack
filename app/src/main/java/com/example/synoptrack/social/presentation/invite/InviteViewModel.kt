package com.example.synoptrack.social.presentation.invite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.social.domain.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<UserProfile?>(null)
    val user: StateFlow<UserProfile?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _requestSent = MutableStateFlow(false)
    val requestSent: StateFlow<Boolean> = _requestSent.asStateFlow()

    fun loadUserByCode(code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            friendRepository.getUserByInviteCode(code)
                .onSuccess { profile ->
                    if (profile == null) {
                        _error.value = "User not found"
                    } else {
                        // Check if it's self
                        if (profile.uid == authRepository.currentUser?.uid) {
                             _error.value = "You cannot add yourself"
                        } else {
                            _user.value = profile
                        }
                    }
                }
                .onFailure {
                    _error.value = it.message
                }
            _isLoading.value = false
        }
    }

    fun sendFriendRequest(targetUid: String) {
        val currentUid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            friendRepository.sendFriendRequest(currentUid, targetUid)
                .onSuccess {
                    _requestSent.value = true
                }
                .onFailure {
                    _error.value = it.message
                }
            _isLoading.value = false
        }
    }
}
