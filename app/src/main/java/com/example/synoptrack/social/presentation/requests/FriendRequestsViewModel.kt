package com.example.synoptrack.social.presentation.requests

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
class FriendRequestsViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _requests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val requests: StateFlow<List<FriendRequest>> = _requests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadRequests()
    }

    private fun loadRequests() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            friendRepository.getPendingRequests(uid).collect { list ->
                _requests.value = list
            }
        }
    }

    fun acceptRequest(requestId: String) {
        android.util.Log.d("RequestsVM", "UI Action: Accept Request $requestId")
        viewModelScope.launch {
            _isLoading.value = true
            friendRepository.acceptFriendRequest(requestId)
                .onFailure { e -> android.util.Log.e("RequestsVM", "Accept failed", e) }
            _isLoading.value = false
        }
    }

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            friendRepository.rejectFriendRequest(requestId)
            _isLoading.value = false
        }
    }
}
