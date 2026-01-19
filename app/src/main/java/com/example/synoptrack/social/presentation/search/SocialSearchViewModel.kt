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
    private val authRepository: AuthRepository,
    private val profileRepository: com.example.synoptrack.profile.domain.repository.ProfileRepository
) : ViewModel() {

    private val _nameQuery = MutableStateFlow("")
    val nameQuery: StateFlow<String> = _nameQuery.asStateFlow()

    private val _tagQuery = MutableStateFlow("")
    val tagQuery: StateFlow<String> = _tagQuery.asStateFlow()
    
    private val _inviteCodeQuery = MutableStateFlow("")
    val inviteCodeQuery: StateFlow<String> = _inviteCodeQuery.asStateFlow()

    private val _ownInviteCode = MutableStateFlow("")
    val ownInviteCode: StateFlow<String> = _ownInviteCode.asStateFlow()

    private val _searchResults = MutableStateFlow<List<UserProfile>>(emptyList())
    val searchResults: StateFlow<List<UserProfile>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _requestStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap()) // uid -> success
    val requestStatus: StateFlow<Map<String, Boolean>> = _requestStatus.asStateFlow()

    private var searchJob: Job? = null
    
    init {
        loadOwnProfile()
    }
    
    private fun loadOwnProfile() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            profileRepository.getUserProfile(uid).collect { profile ->
                if (profile != null) {
                    _ownInviteCode.value = profile.inviteCode
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        _nameQuery.value = newName
        // Manual search button requested? Or debounce? 
        // User said "add a seach button below it and when we use this we will get the result"
        // So maybe disable auto search? 
        // I'll keep debounce for convenience but also allow button click.
        // Actually, let's Stick to Debounce for Name/Tag as it's better UX usually, 
        // but if user insisted on button, I can remove debounce call here.
        // "when we use this we will get the result" -> implies button trigger.
        // I will remove scheduleSearch() from here and put it in a explicit function.
    }
    
    fun onTagChange(newTag: String) {
        if (newTag.length <= 4) {
            _tagQuery.value = newTag
        }
    }
    
    fun onInviteCodeChange(code: String) {
        if (code.length <= 50) {
           _inviteCodeQuery.value = code
        }
    }

    fun performRiotSearch() {
        searchJob?.cancel()
        val name = _nameQuery.value
        val tag = _tagQuery.value
        
        if (name.length >= 3) {
            searchJob = viewModelScope.launch {
                _isLoading.value = true
                performSearch(name, tag)
                _isLoading.value = false
            }
        }
    }
    
    fun performInviteCodeSearch() {
         val code = _inviteCodeQuery.value
         if (code.isNotEmpty()) {
             viewModelScope.launch {
                 _isLoading.value = true
                 friendRepository.getUserByInviteCode(code).onSuccess { user ->
                     if (user != null && user.uid != authRepository.currentUser?.uid) {
                         _searchResults.value = listOf(user)
                     } else {
                         _searchResults.value = emptyList()
                     }
                 }
                 _isLoading.value = false
             }
         }
    }

    private suspend fun performSearch(name: String, tag: String) {
        friendRepository.searchUsers(name, tag)
            .onSuccess { users ->
                 // Filter out self
                 val currentUid = authRepository.currentUser?.uid
                 _searchResults.value = users.filter { it.uid != currentUid }
            }
            .onFailure {
                // Handle error
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
