package com.example.synoptrack.mapos.presentation.model

import com.google.android.gms.maps.model.LatLng

data class MemberUiModel(
    val uid: String,
    val displayName: String,
    val avatarUrl: String,
    val location: LatLng,
    val batteryLevel: Int = -1,
    val isCharging: Boolean = false
)
