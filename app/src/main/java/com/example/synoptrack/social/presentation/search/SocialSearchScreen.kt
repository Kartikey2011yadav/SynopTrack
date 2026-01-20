package com.example.synoptrack.social.presentation.search

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
        onNameChange = { viewModel.onNameChange(it) },
        onTagChange = { viewModel.onTagChange(it) },
        onInviteCodeChange = { viewModel.onInviteCodeChange(it) },
        onSearchRiot = { viewModel.performRiotSearch() },
        onSearchInviteCode = { viewModel.performInviteCodeSearch() },
        onAddFriend = { uid -> viewModel.sendFriendRequest(uid) },
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
    onNameChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onInviteCodeChange: (String) -> Unit,
    onSearchRiot: () -> Unit,
    onSearchInviteCode: () -> Unit,
    onAddFriend: (String) -> Unit,
    onCopyInviteCode: (String) -> Unit
) {
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
                         modifier = Modifier.weight(1f)
                     )
                     Spacer(modifier = Modifier.width(8.dp))
                     SynopTrackTextField(
                         value = tagQuery,
                         onValueChange = onTagChange,
                         label = "Tag",
                         modifier = Modifier.width(100.dp),
                         leadingIcon = { Text("#", modifier = Modifier.padding(start = 12.dp)) }
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

            // 3. Results Section
            if (results.isNotEmpty() || isLoading) {
                item {
                    Text(
                        text = "Results",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(results) { user ->
                        val status = relationshipStatus[user.uid] ?: RelationshipStatus.NONE
                        UserSearchResultItem(
                            user = user,
                            relationshipStatus = status,
                            onAddClick = { onAddFriend(user.uid) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }

             // 4. Invite Code Search Section
             item {
                 Text(
                     text = "Or enter a Friend Code",
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
                     text = "Send Invite",
                     onClick = onSearchInviteCode,
                     enabled = inviteCodeQuery.length > 8,
                     isLoading = isLoading
                 )
                 
                 Spacer(modifier = Modifier.height(32.dp))
             }
         }
    }
}

@Composable
fun UserSearchResultItem(
    user: UserProfile,
    relationshipStatus: RelationshipStatus,
    onAddClick: () -> Unit
) {
    // Privacy Logic: Hide details if Private AND (Not Friend AND Not Self)
    val isPrivate = user.isPrivate
    val canSeeDetails = !isPrivate || relationshipStatus == RelationshipStatus.FRIEND || relationshipStatus == RelationshipStatus.SELF

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                if (canSeeDetails && user.bio.isNotEmpty()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        color = Color.Gray
                    )
                } else if (!canSeeDetails) {
                    Text(
                        text = "Private Account",
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                         color = Color.Gray
                    )
                }
            }
            
            // Action Button
            if (relationshipStatus != RelationshipStatus.SELF) {
                IconButton(
                    onClick = onAddClick,
                    enabled = relationshipStatus == RelationshipStatus.NONE
                ) {
                   when (relationshipStatus) {
                       RelationshipStatus.FRIEND -> Icon(Icons.Default.Group, contentDescription = "Friend", tint = Color.Gray)
                       RelationshipStatus.SENT_REQUEST -> Icon(Icons.Default.Timer, contentDescription = "Sent", tint = Color.Gray)
                       RelationshipStatus.RECEIVED_REQUEST -> Icon(Icons.Default.Check, contentDescription = "Received", tint = Color.Gray) // Could allow accept here too
                       RelationshipStatus.NONE -> Icon(Icons.Default.PersonAdd, contentDescription = "Add Friend", tint = MaterialTheme.colorScheme.primary)
                       else -> {}
                   }
                }
            }
        }
    }
}
