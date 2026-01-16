package com.example.synoptrack.social.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.social.domain.model.FriendRequest
import com.example.synoptrack.social.domain.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _pendingRequests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val pendingRequests: StateFlow<List<FriendRequest>> = _pendingRequests.asStateFlow()

    init {
        loadRequests()
    }

    private fun loadRequests() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            friendRepository.getPendingRequests(uid).collect { requests ->
                _pendingRequests.value = requests
            }
        }
    }

    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            friendRepository.acceptFriendRequest(requestId)
        }
    }

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            friendRepository.rejectFriendRequest(requestId)
        }
    }
}
