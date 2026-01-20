package com.example.synoptrack.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.core.datastore.AppTheme
import com.example.synoptrack.core.datastore.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val themePreferences: ThemePreferences,
    private val profileRepository: com.example.synoptrack.profile.domain.repository.ProfileRepository,
    private val authRepository: com.example.synoptrack.auth.domain.repository.AuthRepository,
    private val socialRepository: com.example.synoptrack.social.domain.repository.SocialRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _logoutEvent = kotlinx.coroutines.channels.Channel<Unit>()
    val logoutEvent = _logoutEvent.receiveAsFlow()

    val currentTheme: StateFlow<AppTheme> = themePreferences.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    init {
        loadUserProfile()
    }

    fun loadUserProfile(targetUserId: String? = null) {
        val currentUid = authRepository.currentUser?.uid ?: return
        val uidToLoad = targetUserId ?: currentUid
        val isMe = uidToLoad == currentUid

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // 1. Load Profile
            profileRepository.getUserProfile(uidToLoad).collect { profile ->
                // Auto-generate Identity for current user if missing
                if (isMe && profile != null && (profile.inviteCode.isEmpty() || profile.discriminator.isEmpty())) {
                    val newDiscriminator = if (profile.discriminator.isEmpty()) com.example.synoptrack.core.utils.IdentityUtils.generateDiscriminator() else profile.discriminator
                    val newInviteCode = if (profile.inviteCode.isEmpty()) com.example.synoptrack.core.utils.IdentityUtils.generateInviteCode(profile.displayName, newDiscriminator) else profile.inviteCode
                    
                    val updatedProfile = profile.copy(
                        discriminator = newDiscriminator,
                        inviteCode = newInviteCode,
                        username = if (profile.username.isEmpty()) profile.displayName else profile.username
                    )
                    profileRepository.saveUserProfile(updatedProfile)
                }

                _uiState.update { 
                    it.copy(
                        user = profile,
                        isCurrentUser = isMe,
                        isLoading = false
                    )
                }
                
                // 2. Check Friendship (if not me)
                if (!isMe && profile != null) {
                    checkFriendship(currentUid, uidToLoad)
                } else {
                    _uiState.update { it.copy(friendshipStatus = FriendshipStatus.SELF) }
                }
            }
        }
    }

    fun updateIdentity(name: String, discriminator: String, bio: String) {
        val currentUser = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Check uniqueness if identity changed
            if (name != currentUser.username || discriminator != currentUser.discriminator) {
                val isAvailable = checkIdentityAvailability(name, discriminator)
                if (!isAvailable) {
                     // TODO: Fix Error Handling in UI State
                     _uiState.update { it.copy(isLoading = false) }
                     return@launch
                }
            }
            
            // If name or discriminator changed, regenerate invite code
            val newInviteCode = if (name != currentUser.username || discriminator != currentUser.discriminator) {
                com.example.synoptrack.core.utils.IdentityUtils.generateInviteCode(name, discriminator)
            } else {
                currentUser.inviteCode
            }

            val updatedProfile = currentUser.copy(
                username = name, 
                displayName = name,
                discriminator = discriminator,
                bio = bio,
                inviteCode = newInviteCode
            )
            
            profileRepository.saveUserProfile(updatedProfile)
                .onSuccess {
                    // UI will update via Flow
                }
                .onFailure {
                    // Handle error
                }
             _uiState.update { it.copy(isLoading = false) }
        }
    }

    suspend fun checkIdentityAvailability(username: String, discriminator: String): Boolean {
         // If checking against self, it's valid if it hasn't changed.
         // But here we are checking availability for a NEW combination or changed one.
         val currentUser = _uiState.value.user
         if (currentUser != null && currentUser.username == username && currentUser.discriminator == discriminator) {
            return true
         }
         return profileRepository.checkIdentityAvailability(username, discriminator).getOrDefault(false)
    }

    private suspend fun checkFriendship(myUid: String, targetUid: String) {
        socialRepository.getFriends(myUid).collect { friends ->
            val isFriend = friends.any { it.uid == targetUid }
            val status = if (isFriend) FriendshipStatus.FRIENDS else FriendshipStatus.NOT_FRIENDS
            // TODO: Add REQUESTED check when available in backend
            _uiState.update { it.copy(friendshipStatus = status) }
        }
    }

    fun uploadProfilePicture(uri: android.net.Uri) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true) }
            
            // Compress
            val bytes = com.example.synoptrack.core.utils.ImageUtils.compressImage(context, uri)
            if (bytes != null) {
                // Upload
                profileRepository.uploadProfilePicture(uid, bytes)
                    .onSuccess { 
                        // Firestore update handled by Repository, Flow will auto-update UI
                    }
                    .onFailure {
                        // Handle error
                    }
            }
            _uiState.update { it.copy(isUploading = false) }
        }
    }

    fun togglePrivacy(isPrivate: Boolean) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            profileRepository.updatePrivacy(uid, isPrivate)
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            themePreferences.setTheme(theme)
        }
    }

    fun logout() {
        viewModelScope.launch {
            profileRepository.logout()
            _logoutEvent.send(Unit)
        }
    }
}

data class ProfileUiState(
    val user: com.example.synoptrack.profile.domain.model.UserProfile? = null,
    val isCurrentUser: Boolean = true,
    val friendshipStatus: FriendshipStatus = FriendshipStatus.SELF,
    val isLoading: Boolean = false,
    val isUploading: Boolean = false
)

enum class FriendshipStatus {
    SELF, NOT_FRIENDS, REQUESTED, FRIENDS
}
