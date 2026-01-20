package com.example.synoptrack.profile.presentation.public_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.synoptrack.core.presentation.components.ButtonVariant
import com.example.synoptrack.core.presentation.components.SynopTrackButton
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.profile.presentation.FriendshipStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    onBack: () -> Unit,
    viewModel: PublicProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.user?.username ?: "Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Report/Block */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val user = uiState.user
            if (user != null) {
                PublicProfileContent(
                    user = user,
                    friendshipStatus = uiState.friendshipStatus,
                    isReceivedRequest = uiState.isReceivedRequest,
                    onSendRequest = viewModel::sendFriendRequest,
                    modifier = Modifier.padding(padding)
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("User not found")
                }
            }
        }
    }
}

@Composable
fun PublicProfileContent(
    user: UserProfile,
    friendshipStatus: FriendshipStatus,
    isReceivedRequest: Boolean,
    onSendRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Avatar
            AsyncImage(
                model = user.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${user.username}" },
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name & Discriminator
            Text(
                text = "${user.username}#${user.discriminator}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Privacy Logic
            val isPrivate = user.isPrivate
            val isFriend = friendshipStatus == FriendshipStatus.FRIENDS
            val canSeeDetails = !isPrivate || isFriend || friendshipStatus == FriendshipStatus.SELF

            if (canSeeDetails && user.bio.isNotEmpty()) {
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else if (!canSeeDetails) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Icon(Icons.Default.Lock, contentDescription = "Private", modifier = Modifier.size(16.dp), tint = Color.Gray)
                     Spacer(modifier = Modifier.width(4.dp))
                     Text(
                        text = "This account is private",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                 }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (friendshipStatus == FriendshipStatus.SELF) {
                    SynopTrackButton(
                        text = "Edit Profile",
                        onClick = { /* Navigate to Edit */ },
                        variant = ButtonVariant.OUTLINED,
                        modifier = Modifier.weight(1f),
                        fullWidth = false
                    )
                } else {
                    // Friend Action
                    val buttonText = when {
                        friendshipStatus == FriendshipStatus.FRIENDS -> "Friends"
                        friendshipStatus == FriendshipStatus.REQUESTED -> "Requested"
                        isReceivedRequest -> "Accept Request"
                        else -> "Add Friend"
                    }
                    
                    val buttonIcon = when {
                        friendshipStatus == FriendshipStatus.FRIENDS -> Icons.Default.Group
                        friendshipStatus == FriendshipStatus.REQUESTED -> Icons.Default.Check
                        isReceivedRequest -> Icons.Default.PersonAdd
                        else -> Icons.Default.PersonAdd
                    }

                    SynopTrackButton(
                        text = buttonText,
                        onClick = onSendRequest, // TODO: different actions for different states
                        icon = buttonIcon,
                        variant = if (friendshipStatus == FriendshipStatus.FRIENDS) ButtonVariant.OUTLINED else ButtonVariant.PRIMARY,
                        enabled = friendshipStatus == FriendshipStatus.NOT_FRIENDS || isReceivedRequest, // Disable if already requested
                         modifier = Modifier.weight(1f),
                         fullWidth = false
                    )
                    
                    if (friendshipStatus == FriendshipStatus.FRIENDS) {
                         SynopTrackButton(
                            text = "Message",
                            onClick = { /* Open Chat */ },
                            icon = Icons.Default.Message,
                            variant = ButtonVariant.PRIMARY,
                            modifier = Modifier.weight(1f),
                            fullWidth = false
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Content Section (Highlights/Posts)
        item {
            val isPrivate = user.isPrivate
            val canSeeDetails = !isPrivate || friendshipStatus == FriendshipStatus.FRIENDS || friendshipStatus == FriendshipStatus.SELF

             if (canSeeDetails) {
                 Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                     Text(
                         text = "Highlights",
                         style = MaterialTheme.typography.titleMedium,
                         fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                     )
                     Spacer(modifier = Modifier.height(16.dp))
                     Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                         repeat(3) {
                             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                  Box(
                                     modifier = Modifier
                                         .size(64.dp)
                                         .clip(CircleShape)
                                         .background(MaterialTheme.colorScheme.surfaceVariant),
                                     contentAlignment = Alignment.Center
                                 ) {
                                     // Placeholder
                                 }
                                 Spacer(modifier = Modifier.height(4.dp))
                                 Text("story", style = MaterialTheme.typography.bodySmall)
                             }
                         }
                     }
                 }
             } else {
                 Column(
                     modifier = Modifier.fillMaxWidth().padding(32.dp),
                     horizontalAlignment = Alignment.CenterHorizontally
                 ) {
                     Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                     Spacer(modifier = Modifier.height(8.dp))
                     Text("Follow this account to see their photos and videos.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.Gray)
                 }
             }
        }
    }
}
