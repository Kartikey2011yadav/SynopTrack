package com.example.synoptrack.mapos.presentation

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.core.location.LocationService
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import com.example.synoptrack.social.domain.model.Group
import com.example.synoptrack.social.domain.repository.SocialRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapOSViewModel @Inject constructor(
    private val locationService: LocationService,
    private val socialRepository: SocialRepository,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val presenceRepository: com.example.synoptrack.core.presence.domain.repository.PresenceRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _lastKnownLocation = MutableStateFlow<LatLng?>(null)
    val lastKnownLocation: StateFlow<LatLng?> = _lastKnownLocation.asStateFlow()

    private val _groupMembers = MutableStateFlow<List<MemberUiModel>>(emptyList())
    val groupMembers: StateFlow<List<MemberUiModel>> = _groupMembers.asStateFlow()

    private val _activeGroup = MutableStateFlow<Group?>(null)
    val activeGroup: StateFlow<Group?> = _activeGroup.asStateFlow()

    private val _isConvoyActive = MutableStateFlow(false)
    val isConvoyActive: StateFlow<Boolean> = _isConvoyActive.asStateFlow()

    private val _isGhostMode = MutableStateFlow(false)
    val isGhostMode: StateFlow<Boolean> = _isGhostMode.asStateFlow()

    val currentUser = authRepository.currentUser

    init {
        startLocationUpdates()
        startSocialUpdates()
        observeUserProfile()
        schedulePassiveUpdates()
    }

    private fun observeUserProfile() {
        val uid = currentUser?.uid ?: return
        viewModelScope.launch {
            profileRepository.getUserProfile(uid).collect { profile ->
                if (profile != null) {
                    _isGhostMode.value = profile.ghostMode
                }
            }
        }
    }

    private fun schedulePassiveUpdates() {
        val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.synoptrack.core.location.PassiveLocationWorker>(
             1, java.util.concurrent.TimeUnit.HOURS
        ).build()
        
        androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "passive_location_updates",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            // This is just for UI (showing Blue Dot), NOT for writing to Firestore
            try {
                locationService.requestLocationUpdates().collect { location ->
                    _lastKnownLocation.value = LatLng(location.latitude, location.longitude)
                }
            } catch (e: Exception) {
                // Likely SecurityException if permissions are missing
                // Or other location service errors.
                // Just swallow it prevents crash.
            }
        }
    }

    private fun startSocialUpdates() {
        val uid = currentUser?.uid ?: return
        viewModelScope.launch {
            socialRepository.getUserGroups(uid).collect { groups ->
                if (groups.isNotEmpty()) {
                    val firstGroup = groups.first()
                    _activeGroup.value = firstGroup
                    observeGroupMembers(firstGroup.id)
                } else {
                    _activeGroup.value = null
                    _groupMembers.value = emptyList()
                }
            }
        }
    }

    private var memberJob: kotlinx.coroutines.Job? = null
    private fun observeGroupMembers(groupId: String) {
        memberJob?.cancel()
        memberJob = viewModelScope.launch {
            socialRepository.getGroupMembers(groupId).collect { profiles ->
                _groupMembers.value = profiles.mapNotNull { profile ->
                    val location = parseLocation(profile.lastLocation)
                    if (location != null && profile.uid != currentUser?.uid) {
                        MemberUiModel(
                            uid = profile.uid,
                            displayName = profile.displayName,
                            avatarUrl = profile.avatarUrl,
                            location = location,
                            batteryLevel = profile.batteryLevel,
                            isCharging = profile.isCharging
                        )
                    } else {
                        null
                    }
                }
            }
        }
    }

    private fun parseLocation(details: Any?): LatLng? {
        if (details is com.google.firebase.firestore.GeoPoint) {
            return LatLng(details.latitude, details.longitude)
        }
        if (details is Map<*, *>) {
            val lat = (details["latitude"] as? Double) ?: return null
            val lng = (details["longitude"] as? Double) ?: return null
            return LatLng(lat, lng)
        }
        return null
    }

    fun createGroup(name: String) {
        val uid = currentUser?.uid ?: return
        viewModelScope.launch {
            socialRepository.createGroup(name, uid)
        }
    }

    fun joinGroup(code: String) {
        val uid = currentUser?.uid ?: return
        viewModelScope.launch {
            socialRepository.joinGroup(code, uid)
        }
    }

    fun startConvoy() {
        _isConvoyActive.value = true
        // Set status to online immediately for UX
         viewModelScope.launch {
             presenceRepository.setOnlineStatus(true)
        }
    }

    fun stopConvoy() {
        _isConvoyActive.value = false
         viewModelScope.launch {
             presenceRepository.setOnlineStatus(false)
        }
    }
}

data class MemberUiModel(
    val uid: String,
    val displayName: String,
    val avatarUrl: String,
    val location: LatLng,
    val batteryLevel: Int = -1,
    val isCharging: Boolean = false
)
