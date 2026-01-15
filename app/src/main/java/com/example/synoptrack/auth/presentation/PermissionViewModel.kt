package com.example.synoptrack.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionUiState())
    val uiState: StateFlow<PermissionUiState> = _uiState.asStateFlow()

    fun checkPermissions(context: android.content.Context) {
        val hasLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val hasNotification = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
             androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true // Not required below A13
        }

        val hasContacts = androidx.core.content.ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.READ_CONTACTS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        _uiState.value = _uiState.value.copy(
            isLocationGranted = hasLocation,
            isNotificationGranted = hasNotification,
            isContactsGranted = hasContacts,
            isPermissionGranted = hasLocation && hasNotification && hasContacts
        )
        
        if (hasLocation) {
             updateGhostMode(false)
        }
    }

    fun onPermissionDenied(shouldShowRationale: Boolean) {
        // ... existing logic can stay or be adapted ...
    }

    private fun updateGhostMode(isGhost: Boolean) {
        viewModelScope.launch {
            val user = authRepository.currentUser
            if (user != null) {
                profileRepository.updateGhostMode(user.uid, isGhost)
            }
        }
    }
    
    // ...
}

data class PermissionUiState(
    val isLocationGranted: Boolean = false,
    val isNotificationGranted: Boolean = false,
    val isContactsGranted: Boolean = false,
    val isPermissionGranted: Boolean = false,
    val isPermanentlyDenied: Boolean = false,
    val isSkipped: Boolean = false,
    val showRationale: Boolean = false,
    val errorMessage: String? = null
)
