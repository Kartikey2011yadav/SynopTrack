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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialSearchScreen(
    onBack: () -> Unit,
    viewModel: SocialSearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val requestStatus by viewModel.requestStatus.collectAsState()

    Scaffold(
        topBar = {
            SearchBar(
                query = query,
                onQueryChange = { viewModel.onQueryChange(it) },
                onSearch = { /* Handled by debounce */ },
                active = true,
                onActiveChange = { },
                placeholder = { Text("Search username#1234") },
                leadingIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            ) {
                 if (isLoading) {
                     Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                         CircularProgressIndicator()
                     }
                 } else {
                     LazyColumn {
                         items(results) { user ->
                             UserSearchResultItem(
                                 user = user,
                                 isRequestSent = requestStatus[user.uid] == true,
                                 onAddClick = { viewModel.sendFriendRequest(user.uid) }
                             )
                         }
                     }
                 }
            }
        }
    ) { padding ->
         // SearchBar handles content
         Box(modifier = Modifier.padding(padding))
    }
}

@Composable
fun UserSearchResultItem(
    user: com.example.synoptrack.profile.domain.model.UserProfile,
    isRequestSent: Boolean,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${user.username}" },
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .fillMaxHeight()
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${user.username}#${user.discriminator}",
                style = MaterialTheme.typography.bodyLarge
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
