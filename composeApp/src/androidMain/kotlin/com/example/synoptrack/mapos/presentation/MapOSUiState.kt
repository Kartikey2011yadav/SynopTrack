package com.example.synoptrack.mapos.presentation

import com.example.synoptrack.profile.domain.model.UserProfile

data class MapOSUiState(
    val showChat: Boolean = false,
    val showProfile: Boolean = false,
    val showMoments: Boolean = false,
    val darkMode: Boolean = false,
    val userProfile: UserProfile? = null
)

