package com.example.synoptrack.social.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
    onNavigateToChat: (String) -> Unit, // New callback
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
        },
        onMessageClick = { uid -> 
            viewModel.onMessageClick(uid, onNavigateToChat)
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
    onCopyInviteCode: (String) -> Unit,
    onMessageClick: (String) -> Unit
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                 },
                 onMessageClick = {
                     onMessageClick(showUserDialog!!.uid)
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
    onViewProfile: () -> Unit,
    onMessageClick: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) {
                    // Prevent dismissal
                },
            contentAlignment = Alignment.TopCenter
        ) {
            // 2. Main Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp) // Space for half-avatar
                    .clickable(onClick = onViewProfile),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp, bottom = 24.dp, start = 24.dp, end = 24.dp), 
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ... (Existing content)
                    Text(
                        text = user.displayName.ifEmpty { user.username },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "@${user.username}#${user.discriminator}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Bio
                    if (user.bio.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = user.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 4,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DialogStatItem("0", "Followers")
                        VerticalDivider(modifier = Modifier.height(24.dp))
                        DialogStatItem("0", "Following")
                        VerticalDivider(modifier = Modifier.height(24.dp))
                        DialogStatItem("0", "Likes")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons (Row)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                         // Message Button (Secondary)
                         SynopTrackButton(
                             text = "Message",
                             onClick = onMessageClick,
                             variant = ButtonVariant.OUTLINED,
                             modifier = Modifier.weight(1f),
                             fullWidth = false,
                             size = com.example.synoptrack.core.presentation.components.ButtonSize.MEDIUM
                         )
                        
                        // Action Button (Primary)
                        val (actionText, action) = when (relationshipStatus) {
                            RelationshipStatus.FRIEND -> "Friends" to { }
                            RelationshipStatus.SENT_REQUEST -> "Cancel" to onCancelRequest
                            RelationshipStatus.RECEIVED_REQUEST -> "Accept" to onAcceptRequest
                            RelationshipStatus.NONE -> "Add Friend" to onAddFriend
                            else -> "" to {}
                        }
                        
                        if (relationshipStatus != RelationshipStatus.SELF) {
                            SynopTrackButton(
                                text = actionText,
                                onClick = action,
                                modifier = Modifier.weight(1f),
                                fullWidth = false,
                                enabled = relationshipStatus != RelationshipStatus.FRIEND, 
                                size = com.example.synoptrack.core.presentation.components.ButtonSize.MEDIUM
                            )
                        }
                    }
                }
            }
            // ... (Avatar and Close button remain same)
             Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    // No padding top, sits at 0 of Box, overlapping Card which starts at 50
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape) // White/Surface border for cutout effect
                    .background(MaterialTheme.colorScheme.surface) // Fallback background
            ) {
                 AsyncImage(
                    model = user.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${user.username}" },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = 40.dp) 
                    .shadow(4.dp, CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun DialogStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
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

@Preview(showBackground = true, name = "UserProfileDialog - Not Friends")
@Composable
fun UserProfileDialogPreview_NotFriends() {
    val user = UserProfile(
        uid = "123",
        username = "TestUser",
        discriminator = "1234",
        bio = "This is a test bio.",
        isPrivate = false,
        avatarUrl = ""
    )
    UserProfileDialog(
        user = user,
        relationshipStatus = RelationshipStatus.NONE,
        onDismiss = { },
        onAddFriend = { },
        onAcceptRequest = { },
        onCancelRequest = { },
        onViewProfile = { },
        onMessageClick = { }
    )
}

@Preview(showBackground = true, name = "UserProfileDialog - Sent Request")
@Composable
fun UserProfileDialogPreview_SentRequest() {
    val user = UserProfile(
        uid = "123",
        username = "TestUser",
        discriminator = "1234",
        bio = "This is a test bio.",
        isPrivate = false,
        avatarUrl = ""
    )
    UserProfileDialog(
        user = user,
        relationshipStatus = RelationshipStatus.SENT_REQUEST,
        onDismiss = { },
        onAddFriend = { },
        onAcceptRequest = { },
        onCancelRequest = { },
        onViewProfile = { },
        onMessageClick = { }
    )
}

@Preview(showBackground = true, name = "UserProfileDialog - Received Request")
@Composable
fun UserProfileDialogPreview_ReceivedRequest() {
    val user = UserProfile(
        uid = "123",
        username = "TestUser",
        discriminator = "1234",
        bio = "This is a test bio.",
        isPrivate = false,
        avatarUrl = ""
    )
    UserProfileDialog(
        user = user,
        relationshipStatus = RelationshipStatus.RECEIVED_REQUEST,
        onDismiss = { },
        onAddFriend = { },
        onAcceptRequest = { },
        onCancelRequest = { },
        onViewProfile = { },
        onMessageClick = { }
    )
}

@Preview(showBackground = true, name = "UserProfileDialog - Friends")
@Composable
fun UserProfileDialogPreview_Friends() {
    val user = UserProfile(
        uid = "123",
        username = "TestUser",
        discriminator = "1234",
        bio = "This is a test bio.",
        isPrivate = false,
        avatarUrl = ""
    )
    UserProfileDialog(
        user = user,
        relationshipStatus = RelationshipStatus.FRIEND,
        onDismiss = { },
        onAddFriend = { },
        onAcceptRequest = { },
        onCancelRequest = { },
        onViewProfile = { },
        onMessageClick = { }
    )
}

@Preview(showBackground = true, name = "UserProfileDialog - Private")
@Composable
fun UserProfileDialogPreview_Private() {
    val user = UserProfile(
        uid = "123",
        username = "TestUser",
        discriminator = "1234",
        bio = "This is a test bio.",
        isPrivate = true,
        avatarUrl = ""
    )
    UserProfileDialog(
        user = user,
        relationshipStatus = RelationshipStatus.NONE,
        onDismiss = { },
        onAddFriend = { },
        onAcceptRequest = { },
        onCancelRequest = { },
        onViewProfile = { },
        onMessageClick = { }
    )
}
