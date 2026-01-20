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

    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        userId?.let { loadProfile(it) }
    }

    private fun loadProfile(uid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Load User Profile
            profileRepository.getUserProfile(uid).collect { profile ->
                _uiState.update { it.copy(user = profile) }
                
                // Check Friendship
                checkFriendshipStatus(uid)
                
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun checkFriendshipStatus(targetUid: String) {
        val currentUid = authRepository.currentUser?.uid ?: return
        
        // We need to check:
        // 1. Are they friends?
        // 2. Did I send a request?
        // 3. Did they send me a request?
        
        // Optimization: ProfileRepository/FriendRepository usually fetches lists.
        // For now, let's use FriendRepository logic if available, or fetch my profile to check lists.
        
        profileRepository.getUserProfile(currentUid).collect { myProfile ->
            val status = when {
                myProfile == null -> FriendshipStatus.NOT_FRIENDS
                myProfile.uid == targetUid -> FriendshipStatus.SELF
                myProfile.friends.contains(targetUid) -> FriendshipStatus.FRIENDS
                myProfile.sentRequests.contains(targetUid) -> FriendshipStatus.REQUESTED
                myProfile.receivedRequests.contains(targetUid) -> FriendshipStatus.FRIENDS // Logic: effectively should just Accept. But for status enum, let's use RECEIVED_REQUEST logic if we had it.
                // Wait, FriendshipStatus enum in ProfileViewModel missing RECEIVED_REQUEST.
                // I should probably move FriendshipStatus to a domain model or shared location.
                // For now, I will map it locally or use String.
                else -> FriendshipStatus.NOT_FRIENDS
            }
            
            // To handle RECEIVED_REQUEST properly, we might need to expand FriendshipStatus or check arrays.
            // If my receivedRequests contains targetUid -> It's a RECEIVED request.
            
            val finalStatus = if (status == FriendshipStatus.NOT_FRIENDS && myProfile?.receivedRequests?.contains(targetUid) == true) {
                 // We don't have RECEIVED in FriendshipStatus enum from ProfileViewModel.
                 // I will define a local/shared one or just assume NOT_FRIENDS but show "Accept" button in UI based on data.
                 FriendshipStatus.NOT_FRIENDS 
            } else {
                status
            }
            
            _uiState.update { it.copy(friendshipStatus = finalStatus, isReceivedRequest = myProfile?.receivedRequests?.contains(targetUid) == true) }
        }
    }

    fun sendFriendRequest() {
        val targetUid = userId ?: return
        val currentUid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            friendRepository.sendFriendRequest(currentUid, targetUid)
                .onSuccess {
                    // UI will update via Flow observation ideally, but for now verify manually
                    _uiState.update { it.copy(friendshipStatus = FriendshipStatus.REQUESTED) }
                }
                .onFailure {
                    // Handle error (Toast)
                }
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
