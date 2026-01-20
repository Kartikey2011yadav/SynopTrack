package com.example.synoptrack.social.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.example.synoptrack.core.presentation.components.ButtonVariant
import com.example.synoptrack.core.presentation.components.SynopTrackButton
import com.example.synoptrack.core.presentation.components.SynopTrackTextField
import com.example.synoptrack.profile.domain.model.UserProfile
import java.util.Collections

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialSearchScreen(
    onBack: () -> Unit,
    onShowQr: () -> Unit,
    onScanQr: () -> Unit,
    onProfileClick: (String) -> Unit,
    viewModel: SocialSearchViewModel = hiltViewModel()
) {
    val nameQuery by viewModel.nameQuery.collectAsState()
    val tagQuery by viewModel.tagQuery.collectAsState()
    val inviteCodeQuery by viewModel.inviteCodeQuery.collectAsState()
    val ownInviteCode by viewModel.ownInviteCode.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val relationshipStatus by viewModel.relationshipStatus.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    SocialSearchScreenContent(
        nameQuery = nameQuery,
        tagQuery = tagQuery,
        inviteCodeQuery = inviteCodeQuery,
        ownInviteCode = ownInviteCode,
        results = results,
        isLoading = isLoading,
        relationshipStatus = relationshipStatus,
        onBack = onBack,
        onShowQr = onShowQr,
        onScanQr = onScanQr,
        onProfileClick = onProfileClick,
        onNameChange = { viewModel.onNameChange(it) },
        onTagChange = { viewModel.onTagChange(it) },
        onInviteCodeChange = { viewModel.onInviteCodeChange(it) },
        onSearchRiot = { viewModel.performRiotSearch() },
        onSearchInviteCode = { viewModel.performInviteCodeSearch() },
        onAddFriend = { uid -> viewModel.sendFriendRequest(uid) },
        onAcceptRequest = { uid -> viewModel.acceptFriendRequest(uid) },
        onCancelRequest = { uid -> viewModel.cancelFriendRequest(uid) },
        onCopyInviteCode = { code ->
            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Friend Code", code)
            clipboard.setPrimaryClip(clip)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialSearchScreenContent(
    nameQuery: String,
    tagQuery: String,
    inviteCodeQuery: String,
    ownInviteCode: String,
    results: List<UserProfile>,
    isLoading: Boolean,
    relationshipStatus: Map<String, RelationshipStatus>,
    onBack: () -> Unit,
    onShowQr: () -> Unit,
    onScanQr: () -> Unit,
    onProfileClick: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onInviteCodeChange: (String) -> Unit,
    onSearchRiot: () -> Unit,
    onSearchInviteCode: () -> Unit,
    onAddFriend: (String) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onCancelRequest: (String) -> Unit,
    onCopyInviteCode: (String) -> Unit
) {
    // State for Dialog
    var showUserDialog by remember { mutableStateOf<UserProfile?>(null) }

    // Effect to show dialog if single result found from Invite Code search (heuristic)
    LaunchedEffect(results) {
        if (results.size == 1 && inviteCodeQuery.isNotEmpty() && results[0].inviteCode == inviteCodeQuery) {
            showUserDialog = results[0]
        }
    }

     Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add a Friend") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
         LazyColumn(
             modifier = Modifier
                 .padding(padding)
                 .fillMaxSize(),
             contentPadding = Collections.nCopies(1, PaddingValues(16.dp))[0]
         ) {
             // 1. Your Code Section
             item {
                 Card(
                     modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                     colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                 ) {
                     Column(modifier = Modifier.padding(16.dp)) {
                         Text(
                             text = "Your Friend Code",
                             style = MaterialTheme.typography.titleMedium,
                             color = MaterialTheme.colorScheme.onSecondaryContainer
                         )
                         Spacer(modifier = Modifier.height(8.dp))
                         Row(
                             verticalAlignment = Alignment.CenterVertically,
                             modifier = Modifier.fillMaxWidth()
                         ) {
                             Text(
                                 text = ownInviteCode,
                                 style = MaterialTheme.typography.displaySmall,
                                 fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                 modifier = Modifier.weight(1f)
                             )
                             SynopTrackButton(
                                 text = "COPY",
                                 onClick = { onCopyInviteCode(ownInviteCode) },
                                 fullWidth = false,
                                 modifier = Modifier.width(100.dp).height(40.dp)
                             )
                         }
                         Spacer(modifier = Modifier.height(16.dp))
                         SynopTrackButton(
                             text = "Show QR Code",
                             onClick = onShowQr,
                             variant = ButtonVariant.OUTLINED
                         )
                     }
                 }
             }

             // 2. Search Section
             item {
                 Text(
                     text = "Search by Riot ID",
                     style = MaterialTheme.typography.titleMedium,
                     modifier = Modifier.padding(bottom = 8.dp)
                 )
                 Row(modifier = Modifier.fillMaxWidth()) {
                     SynopTrackTextField(
                         value = nameQuery,
                         onValueChange = onNameChange,
                         label = "User Name",
                         modifier = Modifier.weight(0.65f) // Adjusted weight
                     )
                     Spacer(modifier = Modifier.width(8.dp))
                     SynopTrackTextField(
                         value = tagQuery,
                         onValueChange = onTagChange,
                         label = "Tag",
                         modifier = Modifier.weight(0.35f), // Adjusted weight
                         leadingIcon = { Text("#", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) }
                     )
                 }
                 Spacer(modifier = Modifier.height(16.dp))
                 SynopTrackButton(
                     text = "Search",
                     onClick = onSearchRiot,
                     icon = Icons.Default.Search,
                     isLoading = isLoading
                 )
                 Spacer(modifier = Modifier.height(24.dp))
             }

             // 3. Results Section (Standard List for Riot Search)
             if (results.isNotEmpty() && inviteCodeQuery.isEmpty()) { // Only show list if NOT invite code search
                item {
                    Text(
                        text = "Results",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(results) { user ->
                    val status = relationshipStatus[user.uid] ?: RelationshipStatus.NONE
                    UserSearchResultItem(
                        user = user,
                        relationshipStatus = status,
                        onAddClick = { onAddFriend(user.uid) },
                        onAcceptClick = { onAcceptRequest(user.uid) },
                        onCancelClick = { onCancelRequest(user.uid) },
                        onClick = { onProfileClick(user.uid) } // Direct navigation for list items
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
             }

             // 4. Invite Code Search Section
             item {
                 Text(
                     text = "Find by Friend Code",
                     style = MaterialTheme.typography.titleMedium,
                     modifier = Modifier.padding(bottom = 8.dp)
                 )
                 SynopTrackTextField(
                     value = inviteCodeQuery,
                     onValueChange = onInviteCodeChange,
                     label = "Friend Code",
                     placeholder = "e.g. User#1234@abcd",
                     modifier = Modifier.fillMaxWidth(),
                     trailingIcon = {
                         IconButton(onClick = onScanQr) {
                             Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR")
                         }
                     }
                 )
                 Spacer(modifier = Modifier.height(16.dp))
                 
                 SynopTrackButton(
                     text = "Find User",
                     onClick = onSearchInviteCode,
                     enabled = inviteCodeQuery.length > 8,
                     isLoading = isLoading
                 )
                 
                 Spacer(modifier = Modifier.height(32.dp))
             }
         }
         
         // User Profile Dialog
         if (showUserDialog != null) {
             UserProfileDialog(
                 user = showUserDialog!!,
                 relationshipStatus = relationshipStatus[showUserDialog!!.uid] ?: RelationshipStatus.NONE,
                 onDismiss = { showUserDialog = null },
                 onAddFriend = { onAddFriend(showUserDialog!!.uid) },
                 onAcceptRequest = { onAcceptRequest(showUserDialog!!.uid) },
                 onCancelRequest = { onCancelRequest(showUserDialog!!.uid) },
                 onViewProfile = {
                     onProfileClick(showUserDialog!!.uid)
                     showUserDialog = null
                 }
             )
         }
    }
}

@Composable
fun UserProfileDialog(
    user: UserProfile,
    relationshipStatus: RelationshipStatus,
    onDismiss: () -> Unit,
    onAddFriend: () -> Unit,
    onAcceptRequest: () -> Unit,
    onCancelRequest: () -> Unit,
    onViewProfile: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onViewProfile), // Make entire dialog content clickable to view profile
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = user.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${user.username}" },
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${user.username}#${user.discriminator}",
                    style = MaterialTheme.typography.headlineSmall,
                     fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                
                // Privacy & Bio Logic
                val isPrivate = user.isPrivate
                val canSeeDetails = !isPrivate || relationshipStatus == RelationshipStatus.FRIEND || relationshipStatus == RelationshipStatus.SELF
                
                Spacer(modifier = Modifier.height(8.dp))
                if (canSeeDetails) {
                    if (user.bio.isNotEmpty()) {
                        Text(
                            text = user.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                     Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                         Spacer(modifier = Modifier.width(4.dp))
                         Text("Private Account", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                     }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Highlights / Stories Placeholder Removed as per request

                // Action Button
                 if (relationshipStatus != RelationshipStatus.SELF) {
                    SynopTrackButton(
                        text = when (relationshipStatus) {
                            RelationshipStatus.FRIEND -> "Message"
                            RelationshipStatus.SENT_REQUEST -> "Cancel Request"
                            RelationshipStatus.RECEIVED_REQUEST -> "Accept Request"
                            else -> "Add Friend"
                        },
                        onClick = {
                             when (relationshipStatus) {
                                RelationshipStatus.NONE -> onAddFriend()
                                RelationshipStatus.RECEIVED_REQUEST -> onAcceptRequest()
                                RelationshipStatus.SENT_REQUEST -> onCancelRequest()
                                else -> {} // Message not impl
                            }
                        },
                        enabled = relationshipStatus != RelationshipStatus.FRIEND, // Disable msg for now
                        variant = if (relationshipStatus == RelationshipStatus.FRIEND) ButtonVariant.OUTLINED else ButtonVariant.PRIMARY
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun UserSearchResultItem(
    user: UserProfile,
    relationshipStatus: RelationshipStatus,
    onAddClick: () -> Unit,
    onAcceptClick: () -> Unit,
    onCancelClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick // Make card clickable
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${user.username}" },
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user.username}#${user.discriminator}",
                    style = MaterialTheme.typography.titleMedium
                )
                // Privacy Logic for List Item (Simplified)
                val isPrivate = user.isPrivate
                val canSeeDetails = !isPrivate || relationshipStatus == RelationshipStatus.FRIEND || relationshipStatus == RelationshipStatus.SELF
                 if (!canSeeDetails) {
                    Text("Private Account", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            
            // Action Icon
            if (relationshipStatus != RelationshipStatus.SELF) {
                IconButton(
                    onClick = {
                        when (relationshipStatus) {
                            RelationshipStatus.NONE -> onAddClick()
                            RelationshipStatus.RECEIVED_REQUEST -> onAcceptClick()
                            RelationshipStatus.SENT_REQUEST -> onCancelClick()
                            else -> {}
                        }
                    },
                    enabled = relationshipStatus != RelationshipStatus.FRIEND
                ) {
                   when (relationshipStatus) {
                       RelationshipStatus.FRIEND -> Icon(Icons.Default.Group, contentDescription = "Friend", tint = MaterialTheme.colorScheme.primary)
                       RelationshipStatus.SENT_REQUEST -> Icon(Icons.Default.Timer, contentDescription = "Sent", tint = Color.Gray)
                       RelationshipStatus.RECEIVED_REQUEST -> Icon(Icons.Default.PersonAdd, contentDescription = "Accept", tint = Color.Green) // Changed icon
                       RelationshipStatus.NONE -> Icon(Icons.Default.PersonAdd, contentDescription = "Add Friend", tint = MaterialTheme.colorScheme.primary)
                       else -> {}
                   }
                }
            }
        }
    }
}
