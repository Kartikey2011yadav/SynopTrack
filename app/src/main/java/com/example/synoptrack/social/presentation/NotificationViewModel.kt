package com.example.synoptrack.social.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.model.NotificationEntity
import com.example.synoptrack.profile.domain.model.NotificationType
import com.example.synoptrack.social.domain.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            friendRepository.getNotifications(uid).collect { list ->
                _notifications.value = list
            }
        }
    }

    fun acceptRequest(notification: NotificationEntity) {
        if (notification.type == NotificationType.FRIEND_REQUEST && notification.actionData != null) {
            viewModelScope.launch {
                friendRepository.acceptFriendRequest(notification.actionData)
                    .onSuccess {
                        // Optimistically update or wait for Flow
                    }
                    .onFailure {
                        // Error handling
                    }
            }
        }
    }

    fun rejectRequest(notification: NotificationEntity) {
        if (notification.type == NotificationType.FRIEND_REQUEST && notification.actionData != null) {
              viewModelScope.launch {
                friendRepository.rejectFriendRequest(notification.actionData)
            }
        }
    }
}
