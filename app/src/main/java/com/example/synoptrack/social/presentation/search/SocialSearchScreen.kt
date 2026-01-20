package com.example.synoptrack.social.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
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
import com.example.synoptrack.profile.domain.model.UserProfile


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
    val requestStatus by viewModel.requestStatus.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    SocialSearchScreenContent(
        nameQuery = nameQuery,
        tagQuery = tagQuery,
        inviteCodeQuery = inviteCodeQuery,
        ownInviteCode = ownInviteCode,
        results = results,
        isLoading = isLoading,
        requestStatus = requestStatus,
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
    requestStatus: Map<String, Boolean>,
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
             contentPadding = java.util.Collections.nCopies(1, PaddingValues(16.dp))[0] // Simple padding
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
                             Button(onClick = { onCopyInviteCode(ownInviteCode) }) {
                                 Text("COPY")
                             }
                         }
                         Spacer(modifier = Modifier.height(8.dp))
                         OutlinedButton(
                             onClick = onShowQr,
                             modifier = Modifier.fillMaxWidth()
                         ) {
                             Text("Show QR Code")
                         }
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
                     OutlinedTextField(
                         value = nameQuery,
                         onValueChange = onNameChange,
                         label = { Text("User Name") },
                         modifier = Modifier.weight(1f),
                         singleLine = true
                     )
                     Spacer(modifier = Modifier.width(8.dp))
                     OutlinedTextField(
                         value = tagQuery,
                         onValueChange = onTagChange,
                         label = { Text("Tag") },
                         modifier = Modifier.width(100.dp),
                         singleLine = true,
                         prefix = { Text("#") }
                     )
                 }
                 Spacer(modifier = Modifier.height(8.dp))
                 Button(
                     onClick = onSearchRiot,
                     modifier = Modifier.fillMaxWidth()
                 ) {
                     Icon(Icons.Default.Search, contentDescription = null)
                     Spacer(modifier = Modifier.width(8.dp))
                     Text("Search")
                 }
                 Spacer(modifier = Modifier.height(24.dp))
             }

            // 3. Results Section (conditionally visible)
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
                        UserSearchResultItem(
                            user = user,
                            isRequestSent = requestStatus[user.uid] == true,
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
                 OutlinedTextField(
                     value = inviteCodeQuery,
                     onValueChange = onInviteCodeChange,
                     label = { Text("Friend Code") },
                     modifier = Modifier.fillMaxWidth(),
                     singleLine = true,
                     placeholder = { Text("e.g. User#1234@abcd") },
                     trailingIcon = {
                         IconButton(onClick = onScanQr) {
                             Icon(androidx.compose.material.icons.Icons.Default.QrCodeScanner, contentDescription = "Scan QR")
                         }
                     }
                 )
                 Spacer(modifier = Modifier.height(8.dp))
                 Button(
                     onClick = onSearchInviteCode,
                     modifier = Modifier.fillMaxWidth(),
                     enabled = inviteCodeQuery.length > 8 // Minimal length for new format
                 ) {
                     Text("Send Invite")
                 }
                 
                 Spacer(modifier = Modifier.height(32.dp))
             }
         }
    }
}

@Composable
fun UserSearchResultItem(
    user: com.example.synoptrack.profile.domain.model.UserProfile,
    isRequestSent: Boolean,
    onAddClick: () -> Unit
) {
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
                if (user.bio.isNotEmpty()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        color = Color.Gray
                    )
                }
            }
            
            IconButton(
                onClick = onAddClick,
                enabled = !isRequestSent
            ) {
                Icon(
                    imageVector = if (isRequestSent) Icons.Default.Check else Icons.Default.PersonAdd,
                    contentDescription = "Add Friend",
                    tint = if (isRequestSent) Color.Green else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun SocialSearchScreenPreview() {
    SocialSearchScreenContent(
        nameQuery = "Test",
        tagQuery = "1234",
        inviteCodeQuery = "",
        ownInviteCode = "Test#1234@CODE",
        results = emptyList(),
        isLoading = false,
        requestStatus = emptyMap(),
        onBack = {},
        onShowQr = {},
        onScanQr = {},
        onNameChange = {},
        onTagChange = {},
        onInviteCodeChange = {},
        onSearchRiot = {},
        onSearchInviteCode = {},
        onAddFriend = {},
        onCopyInviteCode = {}
    )
}
