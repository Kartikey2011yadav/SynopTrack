package com.example.synoptrack.social.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.social.domain.model.Group
import com.example.synoptrack.social.domain.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _friends = MutableStateFlow<List<UserProfile>>(emptyList())
    val friends: StateFlow<List<UserProfile>> = _friends.asStateFlow()

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val uid = authRepository.currentUser?.uid
        if (uid != null) {
            observeFriends(uid)
            observeGroups(uid)
        }
    }

    private fun observeFriends(uid: String) {
        viewModelScope.launch {
            socialRepository.getFriends(uid).collect {
                _friends.value = it
            }
        }
    }

    private fun observeGroups(uid: String) {
        viewModelScope.launch {
            socialRepository.getUserGroups(uid).collect {
                _groups.value = it
            }
        }
    }

    fun addFriend(inviteCode: String) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = socialRepository.addFriend(inviteCode, uid)
            if (result.isSuccess) {
                _toastMessage.value = "Friend added successfully!"
            } else {
                _toastMessage.value = "Failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun createGroup(name: String) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            socialRepository.createGroup(name, uid)
        }
    }

    fun joinGroup(code: String) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            socialRepository.joinGroup(code, uid)
        }
    }
    
    fun clearToast() {
        _toastMessage.value = null
    }
}
