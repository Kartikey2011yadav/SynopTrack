package com.example.synoptrack.social.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.social.domain.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RelationshipStatus {
    NONE,
    FRIEND,
    SENT_REQUEST,
    RECEIVED_REQUEST,
    SELF
}

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
    
    // Status map: TargetUserID -> Status
    private val _relationshipStatus = MutableStateFlow<Map<String, RelationshipStatus>>(emptyMap())
    val relationshipStatus: StateFlow<Map<String, RelationshipStatus>> = _relationshipStatus.asStateFlow()
    
    private var currentUserProfile: UserProfile? = null
    private var searchJob: Job? = null
    
    init {
        loadOwnProfile()
    }
    
    private fun loadOwnProfile() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            profileRepository.getUserProfile(uid).collect { profile ->
                currentUserProfile = profile
                if (profile != null) {
                    _ownInviteCode.value = profile.inviteCode
                    updateRelationshipStatuses(_searchResults.value)
                }
            }
        }
    }
    
    private fun updateRelationshipStatuses(users: List<UserProfile>) {
        val current = currentUserProfile ?: return
        val map = users.associate { user ->
            val status = when {
                user.uid == current.uid -> RelationshipStatus.SELF
                current.friends.contains(user.uid) -> RelationshipStatus.FRIEND
                current.sentRequests.contains(user.uid) -> RelationshipStatus.SENT_REQUEST
                current.receivedRequests.contains(user.uid) -> RelationshipStatus.RECEIVED_REQUEST
                else -> RelationshipStatus.NONE
            }
            user.uid to status
        }
        _relationshipStatus.value = map
    }

    fun onNameChange(newName: String) {
        _nameQuery.value = newName
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
                friendRepository.searchUsers(name, tag)
                    .onSuccess { users ->
                         _searchResults.value = users
                         updateRelationshipStatuses(users)
                    }
                    .onFailure {
                         _searchResults.value = emptyList()
                    }
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
                     if (user != null) {
                         val list = listOf(user)
                         _searchResults.value = list
                         updateRelationshipStatuses(list)
                     } else {
                         _searchResults.value = emptyList()
                     }
                 }
                 _isLoading.value = false
             }
         }
    }

    fun sendFriendRequest(targetUserId: String) {
        val currentUid = authRepository.currentUser?.uid ?: return
        
        // Optimistic Update
        val currentMap = _relationshipStatus.value.toMutableMap()
        currentMap[targetUserId] = RelationshipStatus.SENT_REQUEST
        _relationshipStatus.value = currentMap

        viewModelScope.launch {
            friendRepository.sendFriendRequest(currentUid, targetUserId)
                .onSuccess {
                    // Success
                }
                .onFailure {
                     // Revert on failure
                     val revertMap = _relationshipStatus.value.toMutableMap()
                     revertMap[targetUserId] = RelationshipStatus.NONE
                     _relationshipStatus.value = revertMap
                }
        }
    }
}
