package com.example.synoptrack.mapos.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.synoptrack.profile.domain.model.UserProfile
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapOSScreen(
    viewModel: MapOSViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(22.7196, 75.8577), 15f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Permanent Full-Screen Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false
            )
        )

        // 2. Floating UI Elements

        // Top Bar Area
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FloatingSearchBar(
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                AvatarBubble(
                    onClick = { viewModel.toggleProfile() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ConvoyStatusCard()
        }

        // Floating Action Cluster (Bottom Right)
        FloatingActionCluster(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding(), // Avoid system nav bar overlap
            onChatClick = { viewModel.toggleChat() },
            onMomentsClick = { viewModel.toggleMoments() },
            onRecenterClick = { /* TODO: Recenter map */ }
        )

        // 3. Conditional Modal Bottom Sheets

        if (uiState.showChat) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleChat() }
            ) {
                ChatSheet()
            }
        }

        if (uiState.showProfile) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleProfile() }
            ) {
                ProfileSheet(
                    userProfile = uiState.userProfile,
                    isDarkMode = uiState.darkMode,
                    onThemeToggle = { viewModel.toggleTheme() },
                    onGhostModeToggle = { viewModel.toggleGhostMode(it) },
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    },
                    onMomentsClick = {
                        viewModel.toggleProfile()
                        viewModel.toggleMoments()
                    }
                )
            }
        }

        if (uiState.showMoments) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleMoments() }
            ) {
                MomentsSheet()
            }
        }
    }
}

@Composable
fun FloatingSearchBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(25.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search here...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AvatarBubble(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(50.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ConvoyStatusCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Convoy",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Active Convoy",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "3 members • 5 mins away",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FloatingActionCluster(
    modifier: Modifier = Modifier,
    onChatClick: () -> Unit,
    onMomentsClick: () -> Unit,
    onRecenterClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SmallFloatingActionButton(
            onClick = onMomentsClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.Default.PhotoCamera, "Moments")
        }

        SmallFloatingActionButton(
            onClick = onChatClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.AutoMirrored.Filled.Chat, "Chat")
        }

        FloatingActionButton(
            onClick = onRecenterClick,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.MyLocation, "Recenter")
        }
    }
}

@Composable
fun ChatSheet() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(16.dp)
    ) {
        Text("Chat", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Chat content goes here...")
    }
}


@Composable
fun ProfileSheet(
    userProfile: UserProfile?,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    onGhostModeToggle: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onMomentsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header with Avatar and Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (userProfile?.avatarUrl?.isNotEmpty() == true) {
                AsyncImage(
                    model = userProfile.avatarUrl,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = userProfile?.displayName?.take(1)?.uppercase() ?: "?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = userProfile?.displayName ?: "User",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "SynopTrack Member",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Settings Options

        ListItem(
            headlineContent = { Text("Ghost Mode") },
            supportingContent = { Text("Hide your location from everyone") },
            leadingContent = { Icon(Icons.Default.VisibilityOff, null) },
            trailingContent = {
                Switch(
                    checked = userProfile?.ghostMode ?: false,
                    onCheckedChange = onGhostModeToggle
                )
            }
        )

        ListItem(
            headlineContent = { Text("Dark Mode") },
            leadingContent = { Icon(Icons.Default.DarkMode, null) },
            trailingContent = {
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onThemeToggle() }
                )
            }
        )

        ListItem(
            headlineContent = { Text("Saved Moments") },
            leadingContent = { Icon(Icons.Default.PhotoLibrary, null) },
            modifier = Modifier.clickable { onMomentsClick() }
        )

        ListItem(
            headlineContent = { Text("Privacy Shortcuts") },
            leadingContent = { Icon(Icons.Default.Security, null) },
            modifier = Modifier.clickable { /* TODO */ }
        )

        ListItem(
            headlineContent = { Text("Clear Local Cache") },
            leadingContent = { Icon(Icons.Default.DeleteOutline, null) },
            modifier = Modifier.clickable { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun MomentsSheet() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    ) {
        Text("Moments", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Recent moments from your convoy...")
    }
}

