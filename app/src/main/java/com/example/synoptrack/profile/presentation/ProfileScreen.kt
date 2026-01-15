package com.example.synoptrack.profile.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onEditProfile: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    
    // Image Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { viewModel.uploadProfilePicture(it) } }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (user?.discriminator?.isNotEmpty() ?: false) "${user.username} #${user.discriminator}" else user?.displayName
                            ?: "Profile",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                actions = {
                    if (uiState.isCurrentUser) {
                        IconButton(onClick = { /* New Post */ }) {
                            Icon(Icons.Rounded.Add, contentDescription = "Add")
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                        }
                    } else {
                        // Other user actions (e.g. Report)
                        IconButton(onClick = { /* Report */ }) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "Options")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading || user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 1. Header (Avatar, Stats, Bio)
                ProfileHeader(
                    user = user,
                    isCurrentUser = uiState.isCurrentUser,
                    friendshipStatus = uiState.friendshipStatus,
                    onAvatarClick = {
                        if (uiState.isCurrentUser) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    },
                    onEditProfile = onEditProfile,
                    onShareProfile = { /* TODO */ },
                    onFriendAction = { /* TODO */ }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Private Account Lock Logic
                val isContentVisible = uiState.isCurrentUser || 
                                     !user.isPrivate || 
                                     uiState.friendshipStatus == FriendshipStatus.FRIENDS

                if (isContentVisible) {
                    // Highlights (Placeholder)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(5) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .border(1.dp, Color.Gray, CircleShape)
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Highlight", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentPadding = PaddingValues(1.dp),
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        items(15) {
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                // Placeholder content
                            }
                        }
                    }
                } else {
                    // Private Lock Screen
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Private",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "This account is private",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Follow to see their photos and videos.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: com.example.synoptrack.profile.domain.model.UserProfile,
    isCurrentUser: Boolean,
    friendshipStatus: FriendshipStatus,
    onAvatarClick: () -> Unit,
    onEditProfile: () -> Unit,
    onShareProfile: () -> Unit,
    onFriendAction: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                    .clickable { onAvatarClick() }
            ) {
                if (user.avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person, 
                        contentDescription = null, 
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                // Upload badge for current user
                if (isCurrentUser) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            // Stats
            Row(
                modifier = Modifier.weight(1f).padding(start = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileStat(count = "0", label = "Posts")
                ProfileStat(count = "0", label = "Friends") 
                ProfileStat(count = "0", label = "Trips") 
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Bio & Actions
        Column {
            // Identity Display
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold
                )
            }
            if (user.bio.isNotEmpty()) {
                Text(user.bio, style = MaterialTheme.typography.bodyMedium)
            }
            if (user.inviteCode.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Invite: ${user.inviteCode}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { /* TODO: Show QR Dialog */ }) {
                        Icon(
                            imageVector = Icons.Default.QrCode, // Need to make sure QrCode icon exists or use another
                            contentDescription = "Show QR Code",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action Buttons Logic
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                if (isCurrentUser) {
                    ProfileActionButton(text = "Edit profile", onClick = onEditProfile, modifier = Modifier.weight(1f))
                    ProfileActionButton(text = "Share profile", onClick = onShareProfile, modifier = Modifier.weight(1f))
                } else {
                    when (friendshipStatus) {
                        FriendshipStatus.NOT_FRIENDS -> {
                            Button(
                                onClick = onFriendAction,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Add Friend")
                            }
                        }
                        FriendshipStatus.REQUESTED -> {
                            ProfileActionButton(text = "Requested", onClick = {}, modifier = Modifier.weight(1f))
                        }
                        FriendshipStatus.FRIENDS -> {
                            ProfileActionButton(text = "Message", onClick = {}, modifier = Modifier.weight(1f))
                            ProfileActionButton(text = "Unfriend", onClick = {}, modifier = Modifier.weight(1f))
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStat(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ProfileActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(34.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}
