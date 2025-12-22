package com.example.synoptrack.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _isLocationSharing = mutableStateOf(false)
    val isLocationSharing: State<Boolean> = _isLocationSharing

    fun toggleLocationSharing() {
        _isLocationSharing.value = !_isLocationSharing.value
    }
}