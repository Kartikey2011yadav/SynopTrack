package com.example.synoptrack.social.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.social.domain.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialSearchViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<List<UserProfile>>(emptyList())
    val searchResults: StateFlow<List<UserProfile>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _requestStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap()) // uid -> success
    val requestStatus: StateFlow<Map<String, Boolean>> = _requestStatus.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()
        if (newQuery.length > 2) {
            searchJob = viewModelScope.launch {
                delay(500) // Debounce
                performSearch(newQuery)
            }
        } else {
            _searchResults.value = emptyList()
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            friendRepository.searchUsers(query)
                .onSuccess { users ->
                     // Filter out self
                     val currentUid = authRepository.currentUser?.uid
                     _searchResults.value = users.filter { it.uid != currentUid }
                }
                .onFailure {
                    // Handle error
                }
            _isLoading.value = false
        }
    }

    fun sendFriendRequest(targetUserId: String) {
        val currentUid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            friendRepository.sendFriendRequest(currentUid, targetUserId)
                .onSuccess {
                    _requestStatus.value += (targetUserId to true)
                }
                .onFailure {
                     // Show error
                }
        }
    }
}
