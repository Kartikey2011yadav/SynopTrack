package com.example.synoptrack.social.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.synoptrack.core.theme.LiveTeal
import com.example.synoptrack.mapos.presentation.MapOSViewModel
import com.example.synoptrack.social.presentation.components.AddFriendDialog
import com.example.synoptrack.social.presentation.components.CreateGroupDialog
import com.example.synoptrack.social.presentation.components.JoinGroupDialog

@Composable
fun SocialScreen(
    mapViewModel: MapOSViewModel = hiltViewModel(),
    socialViewModel: SocialViewModel = hiltViewModel(),
    onChatClick: (String) -> Unit = {}
) {
    val activeGroup by mapViewModel.activeGroup.collectAsState()
    val groupMembers by mapViewModel.groupMembers.collectAsState()
    val isConvoyActive by mapViewModel.isConvoyActive.collectAsState()

    val friends by socialViewModel.friends.collectAsState()
    val groups by socialViewModel.groups.collectAsState()
    val toastMessage by socialViewModel.toastMessage.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showAddFriendDialog by remember { mutableStateOf(false) }

    // Toast Effect
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            // In a real app, use SnackbarHostState
             socialViewModel.clearToast()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp) // Status bar spacing
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Social", 
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Header Actions
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { showAddFriendDialog = true }) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Add Friend", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { showJoinDialog = true }) {
                    Icon(Icons.Default.GroupAdd, contentDescription = "Join Group", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Active Convoy Card (If Active)
        if (isConvoyActive && activeGroup != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clickable { onChatClick(activeGroup!!.id) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer) // Red for Active
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                         modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("LIVE Tracking Active", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                        Text(activeGroup!!.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { mapViewModel.stopConvoy() }) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Section: Chats (Friends & Groups Mixed)
        Text(
            "Chats",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // GROUPS
            items(groups) { group ->
                SocialListItem(
                    name = group.name,
                    subtitle = "${group.memberIds.size} members",
                    initial = group.name.take(1).uppercase(),
                    isOnline = (activeGroup?.id == group.id && isConvoyActive),
                    onClick = { onChatClick(group.id) }
                )
            }
            
            // FRIENDS
            items(friends) { friend ->
                SocialListItem(
                    name = friend.displayName.ifEmpty { "Unknown" },
                    subtitle = if (friend.isCharging) "Charging..." else "Offline", // Placeholder status
                    initial = friend.displayName.take(1).uppercase(),
                    imageUrl = friend.avatarUrl,
                    isOnline = false, // Todo: Real Presence
                    onClick = { onChatClick(friend.uid) } // Using UID as Chat ID for now
                )
            }
            
            if (groups.isEmpty() && friends.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.synoptrack.R.drawable.messages),
                                contentDescription = "No Messages",
                                modifier = Modifier.size(200.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                             )
                             Spacer(modifier = Modifier.height(16.dp))
                             Text("No messages yet.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                             Text("Add friends or join groups to start chatting.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                             Spacer(modifier = Modifier.height(16.dp))
                             Button(onClick = { showCreateDialog = true }) {
                                 Text("Create New Group")
                             }
                         }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateGroupDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                socialViewModel.createGroup(name)
                showCreateDialog = false
            }
        )
    }

    if (showJoinDialog) {
        JoinGroupDialog(
            onDismiss = { showJoinDialog = false },
            onJoin = { code ->
                socialViewModel.joinGroup(code)
                showJoinDialog = false
            }
        )
    }
    
    if (showAddFriendDialog) {
        AddFriendDialog(
            onDismiss = { showAddFriendDialog = false },
            onAdd = { code ->
                socialViewModel.addFriend(code)
                showAddFriendDialog = false
            }
        )
    }
}

@Composable
fun SocialListItem(
    name: String,
    subtitle: String,
    initial: String,
    imageUrl: String = "",
    isOnline: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (imageUrl.isNotEmpty()) {
                    // Start of Image Loading (Placeholder)
                     Text(initial, style = MaterialTheme.typography.titleLarge)
                } else {
                    Text(initial, style = MaterialTheme.typography.titleLarge)
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        if (isOnline) {
             Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(LiveTeal)
            )
        }
    }
}
