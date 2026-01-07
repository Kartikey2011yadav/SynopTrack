package com.example.synoptrack.mapos.presentation

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.core.location.LocationService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapOSViewModel @Inject constructor(
    private val locationService: LocationService
) : ViewModel() {

    private val _lastKnownLocation = MutableStateFlow<LatLng?>(null)
    val lastKnownLocation: StateFlow<LatLng?> = _lastKnownLocation.asStateFlow()

    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            locationService.requestLocationUpdates().collect { location ->
                _lastKnownLocation.value = LatLng(location.latitude, location.longitude)
            }
        }
    }
}
