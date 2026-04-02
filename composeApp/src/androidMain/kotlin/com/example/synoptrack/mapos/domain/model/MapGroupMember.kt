package com.example.synoptrack.mapos.domain.model

import com.google.android.gms.maps.model.LatLng

data class MapGroupMember(
    val id: String,
    val displayName: String,
    val location: LatLng,
    val batteryLevel: Int,
    val isCharging: Boolean
)
