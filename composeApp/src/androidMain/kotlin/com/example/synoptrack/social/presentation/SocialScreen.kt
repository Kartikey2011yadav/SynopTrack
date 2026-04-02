package com.example.synoptrack.social.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.synoptrack.mapos.presentation.MapOSViewModel
import com.example.synoptrack.social.domain.model.Group
import com.example.synoptrack.social.presentation.components.AddFriendDialog
import com.example.synoptrack.social.presentation.components.CreateGroupDialog
import com.example.synoptrack.social.presentation.components.JoinGroupDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SocialScreen(
    mapViewModel: MapOSViewModel = hiltViewModel(),
    socialViewModel: SocialViewModel = hiltViewModel(),
    onChatClick: (String) -> Unit = {}
) {
    val activeGroup by mapViewModel.activeGroup.collectAsState()
    val isConvoyActive by mapViewModel.isConvoyActive.collectAsState()
    val chatItems by socialViewModel.chatItems.collectAsState()
    val searchQuery by socialViewModel.searchQuery.collectAsState()
    val selectedFilter by socialViewModel.selectedFilter.collectAsState()
    val toastMessage by socialViewModel.toastMessage.collectAsState()

    // Toast Effect
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
             socialViewModel.clearToast()
        }
    }

    SocialScreenContent(
        activeGroup = activeGroup,
        isConvoyActive = isConvoyActive,
        chatItems = chatItems,
        searchQuery = searchQuery,
        selectedFilter = selectedFilter,
        onSearchQueryChange = { socialViewModel.onSearchQueryChange(it) },
        onFilterSelected = { socialViewModel.onFilterSelected(it) },
        onChatClick = { target ->
             if (target.startsWith("NEW:")) {
                 val userId = target.removePrefix("NEW:")
                 socialViewModel.startNewChat(userId, onChatClick)
             } else {
                 val chatId = target.removePrefix("CHAT:")
                 // Verify if it's a generic string or ID. If just string, assume ID.
                 onChatClick(chatId)
             }
        },
        onStopConvoy = { mapViewModel.stopConvoy() },
        onCreateGroup = { name -> socialViewModel.createGroup(name) },
        onJoinGroup = { code -> socialViewModel.joinGroup(code) },
        onAddFriend = { code -> socialViewModel.addFriend(code) }
    )
}

@Composable
fun SocialScreenContent(
    activeGroup: Group?,
    isConvoyActive: Boolean,
    chatItems: List<com.example.synoptrack.social.presentation.ChatItemState>,
    searchQuery: String,
    selectedFilter: com.example.synoptrack.social.presentation.ChatFilter,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelected: (com.example.synoptrack.social.presentation.ChatFilter) -> Unit,
    onChatClick: (String) -> Unit,
    onStopConvoy: () -> Unit,
    onCreateGroup: (String) -> Unit,
    onJoinGroup: (String) -> Unit,
    onAddFriend: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showAddFriendDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp) 
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Messages", 
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { /* Camera? */ }) {
                    Icon(Icons.Outlined.CameraAlt, contentDescription = "Camera", tint = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Outlined.Edit, contentDescription = "New Message", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        // Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == com.example.synoptrack.social.presentation.ChatFilter.ALL,
                onClick = { onFilterSelected(com.example.synoptrack.social.presentation.ChatFilter.ALL) },
                label = "All"
            )
            FilterChip(
                selected = selectedFilter == com.example.synoptrack.social.presentation.ChatFilter.CONTACTS,
                onClick = { onFilterSelected(com.example.synoptrack.social.presentation.ChatFilter.CONTACTS) },
                label = "Contacts"
            )
            FilterChip(
                selected = selectedFilter == com.example.synoptrack.social.presentation.ChatFilter.UNKNOWN,
                onClick = { onFilterSelected(com.example.synoptrack.social.presentation.ChatFilter.UNKNOWN) },
                label = "Unknown"
            )
             FilterChip(
                selected = selectedFilter == com.example.synoptrack.social.presentation.ChatFilter.NEW,
                onClick = { onFilterSelected(com.example.synoptrack.social.presentation.ChatFilter.NEW) },
                label = "New"
            )
        }

        // Active Convoy Card (If Active)
        if (isConvoyActive && activeGroup != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clickable { onChatClick(activeGroup.id) },
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
                        Text(activeGroup.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onStopConvoy) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Chat List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(chatItems) { item ->
                MessageListItem(
                    item = item,
                    onClick = { 
                        if (item.id.isNotEmpty()) {
                            onChatClick("CHAT:${item.id}")
                        } else {
                            onChatClick("NEW:${item.targetUserId}")
                        }
                    }
                )
            }
        }
    }

    // Dialogs ... (Keep existing)
    if (showCreateDialog) {
         CreateGroupDialog(onDismiss={showCreateDialog=false}, onCreate={onCreateGroup(it);showCreateDialog=false})
    }
    if (showJoinDialog) {
         JoinGroupDialog(onDismiss={showJoinDialog=false}, onJoin={onJoinGroup(it);showJoinDialog=false})
    }
    if (showAddFriendDialog) {
         AddFriendDialog(onDismiss={showAddFriendDialog=false}, onAdd={onAddFriend(it);showAddFriendDialog=false})
    }
}

@Composable
fun FilterChip(selected: Boolean, onClick: () -> Unit, label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.clickable(onClick = onClick),
        border = if (selected) null else BorderStroke(1.dp, Color.LightGray)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun MessageListItem(
    item: com.example.synoptrack.social.presentation.ChatItemState,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box {
             AsyncImage(
                model = item.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${item.name}" },
                contentDescription = null,
                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.Gray),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            // Online Status Dot
            if (item.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.lastMessage,
                style = if (item.unreadCount > 0) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium,
                color = if (item.unreadCount > 0) MaterialTheme.colorScheme.onSurface else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        // Metadata
        Column(horizontalAlignment = Alignment.End) {
            val dateText = remember(item.timestamp) {
                // Simple formatting logic
                if (item.timestamp == null) "Now" else {
                    val now = Date()
                    val diff = now.time - item.timestamp.time
                    when {
                        diff < 60 * 60 * 1000L -> "${diff / (60 * 1000)}m ago"
                        diff < 24 * 60 * 60 * 1000L -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(item.timestamp)
                        else -> "Yesterday" // Simplified
                    }
                }
            }
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodySmall,
                color = if (item.unreadCount > 0) MaterialTheme.colorScheme.primary else Color.Gray
            )
            
            if (item.unreadCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Preview
@Composable

fun SocialScreenPreview() {
    SocialScreenContent(
        activeGroup = null,
        isConvoyActive = false,
        chatItems = emptyList(),
        searchQuery = "",
        selectedFilter = com.example.synoptrack.social.presentation.ChatFilter.ALL,
        onSearchQueryChange = {},
        onFilterSelected = {},
        onChatClick = {},
        onStopConvoy = {},
        onCreateGroup = {},
        onJoinGroup = {},
        onAddFriend = {}
    )
}
