package com.example.synoptrack.profile.presentation.public_profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.synoptrack.R
import com.example.synoptrack.core.presentation.components.ButtonVariant
import com.example.synoptrack.core.theme.SynopTrackTheme
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
            SynopTrackTopBar(
                title = uiState.user?.username ?: "Profile",
                onBack = onBack,
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
                    onAcceptRequest = viewModel::acceptFriendRequest,
                    onCancelRequest = viewModel::cancelFriendRequest,
                    onRemoveFriend = viewModel::removeFriend,
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
    onAcceptRequest: () -> Unit,
    onCancelRequest: () -> Unit,
    onRemoveFriend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // 1. Header (Avatar, Stats, Bio) - Matching ProfileHeader
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
                ) {
                    AsyncImage(
                        model = user.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${user.username}" },
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Stats
                Row(
                    modifier = Modifier.weight(1f).padding(start = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PublicProfileStat(count = "0", label = "Posts")
                    PublicProfileStat(count = "0", label = "Friends") 
                    PublicProfileStat(count = "0", label = "Trips") 
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Bio & Actions
            Column {
                // Identity Display
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold
                )
                if (user.bio.isNotEmpty()) {
                    Text(user.bio, style = MaterialTheme.typography.bodyMedium)
                }
                if (user.inviteCode.isNotEmpty()) {
                     Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Invite: ${user.inviteCode}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Action Buttons Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    // Button 1: Friendship Status
                    val (text, action, _) = when {
                        friendshipStatus == FriendshipStatus.FRIENDS -> Triple("Friends", onRemoveFriend, ButtonVariant.OUTLINED) // Or "Remove" in menu
                        friendshipStatus == FriendshipStatus.REQUESTED -> Triple("Requested", onCancelRequest, ButtonVariant.OUTLINED)
                        isReceivedRequest -> Triple("Accept Request", onAcceptRequest, ButtonVariant.PRIMARY)
                        else -> Triple("Add Friend", onSendRequest, ButtonVariant.PRIMARY)
                    }
                    
                    if (friendshipStatus == FriendshipStatus.NOT_FRIENDS && !isReceivedRequest) {
                         // Blue Button for Add Friend
                         Button(
                            onClick = action,
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                        }
                    } else if (isReceivedRequest) {
                        // Blue Button for Accept
                         Button(
                            onClick = action,
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                        }
                    } else {
                        // Gray Button for Requested / Friends
                         PublicProfileActionButton(text = text, onClick = action, modifier = Modifier.weight(1f))
                    }

                    PublicProfileActionButton(text = "Message", onClick = { /* TODO */ }, modifier = Modifier.weight(1f))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Privacy Logic
        val isPrivate = user.isPrivate
        val isFriend = friendshipStatus == FriendshipStatus.FRIENDS
        val canSeeDetails = !isPrivate || isFriend || friendshipStatus == FriendshipStatus.SELF

        if (canSeeDetails) {
             // Highlights
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

            // Posts (Empty State)
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.empty_posts),
                    contentDescription = "No Posts",
                    modifier = Modifier.size(250.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                   text = "No posts to see here yet.",
                   style = MaterialTheme.typography.bodyLarge,
                   color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
             // Private Lock
             Box(
                 modifier = Modifier.fillMaxWidth().weight(1f),
                 contentAlignment = Alignment.Center
             ) {
                 Column(horizontalAlignment = Alignment.CenterHorizontally) {
                     Icon(Icons.Default.Lock, contentDescription = "Private", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                     Spacer(modifier = Modifier.height(8.dp))
                     Text(
                        text = "This account is private",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                     Text(
                        text = "Follow to see their photos and videos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                 }
            }
        }
    }
}

@Composable
fun PublicProfileStat(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PublicProfileActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(34.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Preview(showBackground = true)
@Composable
fun PublicProfileScreenNotFriendPreview() {
    SynopTrackTheme {
        PublicProfileContent(
            user = UserProfile(
//                id = "1",
                username = "JohnDoe",
                displayName = "John Doe",
                bio = "Lover of coffee and code.",
                avatarUrl = "",
                isPrivate = false,
                inviteCode = "ABC.123"
            ),
            friendshipStatus = FriendshipStatus.NOT_FRIENDS,
            isReceivedRequest = false,
            onSendRequest = {},
            onAcceptRequest = {},
            onCancelRequest = {},
            onRemoveFriend = {}
        )
    }
}
