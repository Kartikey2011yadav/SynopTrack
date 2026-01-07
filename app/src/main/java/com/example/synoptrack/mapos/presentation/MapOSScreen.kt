package com.example.synoptrack.mapos.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.synoptrack.core.utils.MapStyleManager
import com.example.synoptrack.mapos.presentation.components.SearchBar
import com.example.synoptrack.social.presentation.components.CreateGroupDialog
import com.example.synoptrack.social.presentation.components.JoinGroupDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun MapOSScreen(
    viewModel: MapOSViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 11f)
    }

    val lastLocation by viewModel.lastKnownLocation.collectAsState()
    val hasSetInitialCamera = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(lastLocation) {
        lastLocation?.let { loc ->
            if (!hasSetInitialCamera.value) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(loc, 15f),
                    1000
                )
                hasSetInitialCamera.value = true
            }
        }
    }

    // Service Control Logic
    val isGhostMode by viewModel.isGhostMode.collectAsState()
    
    LaunchedEffect(isGhostMode) {
        val intent = android.content.Intent(context, com.example.synoptrack.core.presence.service.PresenceForegroundService::class.java)
        if (isGhostMode) {
            intent.action = com.example.synoptrack.core.presence.service.PresenceForegroundService.ACTION_STOP
            context.startService(intent) // Triggers onStartCommand with STOP action
        } else {
            // Start Service if not in Ghost Mode (and permission granted - assumed for now or handled by service)
            intent.action = com.example.synoptrack.core.presence.service.PresenceForegroundService.ACTION_START
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    // specific Map Properties that react to theme changes
    val mapProperties = remember(isDarkTheme) { 
        MapProperties(
            isMyLocationEnabled = true, // Enable Blue Dot
            mapStyleOptions = MapStyleManager.getMapStyle(context, isDarkTheme)
        )
    }
    
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = false,
                myLocationButtonEnabled = false
            )
        )
    }

    val groupMembers by viewModel.groupMembers.collectAsState()
    val activeGroup by viewModel.activeGroup.collectAsState()
    
    var showSocialOptions by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings
        ) {
            // Render Friend Markers
            groupMembers.forEach { member ->
                val batteryInfo = if (member.batteryLevel >= 0) {
                     "🔋 ${member.batteryLevel}%" + if (member.isCharging) " ⚡" else ""
                } else "Unknown"
                
                com.google.maps.android.compose.Marker(
                    state = com.google.maps.android.compose.MarkerState(position = member.location),
                    title = member.displayName,
                    snippet = batteryInfo, 
                    icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE)
                )
            }
        }
        
        // Top Layer: Search Bar
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            onMenuClick = { showSocialOptions = true },
            onSearchClick = { /* Open Search */ }
        )

        // Social FAB (above discovery overlay)
        androidx.compose.material3.FloatingActionButton(
            onClick = { showSocialOptions = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 120.dp, end = 16.dp),
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Group,
                contentDescription = "Social"
            )
        }

        // My Location FAB
        androidx.compose.material3.SmallFloatingActionButton(
            onClick = { 
                val loc = lastLocation
                if (loc != null) {
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(loc, 15f),
                            1000
                        )
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 180.dp, end = 24.dp), // Position above Social FAB
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Icon(
                 imageVector = Icons.Default.MyLocation,
                 contentDescription = "My Location"
            )
        }

        // Dialogs
        if (showSocialOptions) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showSocialOptions = false },
                title = { androidx.compose.material3.Text("Convoy Options") },
                text = {
                    androidx.compose.foundation.layout.Column {
                        if (activeGroup != null) {
                            androidx.compose.material3.Text("Active Group: ${activeGroup?.name}")
                            androidx.compose.material3.Text("Code: ${activeGroup?.inviteCode}")
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                            androidx.compose.material3.Button(onClick = { /* Leave Group TODO */ }) {
                                androidx.compose.material3.Text("Leave Convoy")
                            }
                        } else {
                            androidx.compose.material3.Button(
                                onClick = { 
                                    showSocialOptions = false
                                    showCreateDialog = true 
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                androidx.compose.material3.Text("Create Convoy")
                            }
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                            androidx.compose.material3.OutlinedButton(
                                onClick = { 
                                    showSocialOptions = false
                                    showJoinDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                androidx.compose.material3.Text("Join Convoy")
                            }
                        }
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { showSocialOptions = false }) {
                        androidx.compose.material3.Text("Close")
                    }
                }
            )
        }

        if (showCreateDialog) {
            com.example.synoptrack.social.presentation.components.CreateGroupDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name ->
                    viewModel.createGroup(name)
                    showCreateDialog = false
                }
            )
        }

        if (showJoinDialog) {
            com.example.synoptrack.social.presentation.components.JoinGroupDialog(
                onDismiss = { showJoinDialog = false },
                onJoin = { code ->
                    viewModel.joinGroup(code)
                    showJoinDialog = false
                }
            )
        }
    }
}
