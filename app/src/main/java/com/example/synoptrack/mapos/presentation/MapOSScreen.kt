package com.example.synoptrack.mapos.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import com.example.synoptrack.mapos.presentation.components.HomeTopBar
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
    val isConvoyActive by viewModel.isConvoyActive.collectAsState()
    
    LaunchedEffect(isConvoyActive) {
        val intent = android.content.Intent(context, com.example.synoptrack.core.presence.service.PresenceForegroundService::class.java)
        if (!isConvoyActive) {
            intent.action = com.example.synoptrack.core.presence.service.PresenceForegroundService.ACTION_STOP_CONVOY
            context.startService(intent)
        } else {
            // Check permissions before starting foreground service
            val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                intent.action = com.example.synoptrack.core.presence.service.PresenceForegroundService.ACTION_START_CONVOY
                context.startForegroundService(intent)
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
                
                Marker(
                    state = MarkerState(position = member.location),
                    title = member.displayName,
                    snippet = batteryInfo, 
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
        }
        
        // Top Layer: Instagram-style Header
        HomeTopBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            onAddClick = { showCreateDialog = true }, // Or Navigate to "New Post/Status"
            onSocialClick = { showSocialOptions = true } // Eventually navigate to SocialScreen
        )

        // My Location FAB
        SmallFloatingActionButton(
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
            AlertDialog(
                onDismissRequest = { showSocialOptions = false },
                title = { Text("Convoy Options") },
                text = {
                    androidx.compose.foundation.layout.Column {
                        if (activeGroup != null) {
                            Text("Active Group: ${activeGroup?.name}")
                            Text("Code: ${activeGroup?.inviteCode}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { /* Leave Group TODO */ }) {
                                Text("Leave Convoy")
                            }
                        } else {
                            Button(
                                onClick = {
                                    showSocialOptions = false
                                    showCreateDialog = true 
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Create Convoy")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    showSocialOptions = false
                                    showJoinDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Join Convoy")
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSocialOptions = false }) {
                        Text("Close")
                    }
                }
            )
        }

        if (showCreateDialog) {
            CreateGroupDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name ->
                    viewModel.createGroup(name)
                    showCreateDialog = false
                }
            )
        }

        if (showJoinDialog) {
            JoinGroupDialog(
                onDismiss = { showJoinDialog = false },
                onJoin = { code ->
                    viewModel.joinGroup(code)
                    showJoinDialog = false
                }
            )
        }
    }
}
