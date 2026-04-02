package com.example.synoptrack.profile.presentation.public_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import com.example.synoptrack.profile.presentation.FriendshipStatus
import com.example.synoptrack.social.domain.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val friendRepository: FriendRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String? = savedStateHandle["userId"]

    private val _uiState = MutableStateFlow(PublicProfileUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        userId?.let { uid ->
             loadProfile(uid)
        }
    }

    private fun loadProfile(uid: String) {
        val currentUid = authRepository.currentUser?.uid
        
        if (currentUid == null) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            // Combine target profile and my profile to determine state
            val targetProfileFlow = profileRepository.getUserProfile(uid)
            val myProfileFlow = profileRepository.getUserProfile(currentUid)

            kotlinx.coroutines.flow.combine(targetProfileFlow, myProfileFlow) { targetProfile, myProfile ->
                // Calculate Friendship Status
                val status = when {
                    myProfile == null || targetProfile == null -> FriendshipStatus.NOT_FRIENDS
                    myProfile.uid == targetProfile.uid -> FriendshipStatus.SELF
                    myProfile.friends.contains(targetProfile.uid) -> FriendshipStatus.FRIENDS
                    myProfile.sentRequests.contains(targetProfile.uid) -> FriendshipStatus.REQUESTED
                    // logic for received request check
                    else -> FriendshipStatus.NOT_FRIENDS
                }
                
                val isReceived = myProfile?.receivedRequests?.contains(targetProfile?.uid) == true

                PublicProfileUiState(
                    user = targetProfile,
                    isLoading = false,
                    friendshipStatus = if (status == FriendshipStatus.NOT_FRIENDS && isReceived) FriendshipStatus.NOT_FRIENDS else status,
                    isReceivedRequest = isReceived
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun sendFriendRequest() {
        val targetUid = userId ?: return
        val currentUid = authRepository.currentUser?.uid ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            friendRepository.sendFriendRequest(currentUid, targetUid)
                .onFailure {
                    // handle error
                }
             _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun acceptFriendRequest() {
        val targetUid = userId ?: return
        val currentUid = authRepository.currentUser?.uid ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            friendRepository.acceptFriendRequestByUserId(currentUid, targetUid)
             _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun cancelFriendRequest() {
        val targetUid = userId ?: return
        val currentUid = authRepository.currentUser?.uid ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            friendRepository.cancelFriendRequestByUserId(currentUid, targetUid)
             _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun removeFriend() {
        val targetUid = userId ?: return
        val currentUid = authRepository.currentUser?.uid ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            friendRepository.removeFriend(currentUid, targetUid)
             _uiState.update { it.copy(isLoading = false) }
        }
    }
}

data class PublicProfileUiState(
    val user: UserProfile? = null,
    val isLoading: Boolean = false,
    val friendshipStatus: FriendshipStatus = FriendshipStatus.NOT_FRIENDS,
    val isReceivedRequest: Boolean = false
)
