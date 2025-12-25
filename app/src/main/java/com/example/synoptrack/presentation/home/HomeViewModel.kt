package com.example.synoptrack.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class Group(
    val id: String,
    val name: String,
    val imageUrl: String,
    val activeMembers: Int
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _isLocationSharing = mutableStateOf(false)
    val isLocationSharing: State<Boolean> = _isLocationSharing

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    init {
        loadDummyGroups()
    }

    fun toggleLocationSharing() {
        _isLocationSharing.value = !_isLocationSharing.value
    }

    private fun loadDummyGroups() {
        _groups.value = listOf(
            Group("1", "Road Trip", "", 3),
            Group("2", "College Squad", "", 0),
            Group("3", "Family", "", 5),
            Group("4", "Work Colleagues", "", 1),
            Group("5", "Gaming Crew", "", 0)
        )
    }
}