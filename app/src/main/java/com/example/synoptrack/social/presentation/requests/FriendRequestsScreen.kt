package com.example.synoptrack.social.presentation.requests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import coil.compose.AsyncImage
import com.example.synoptrack.core.presentation.components.ButtonVariant
import com.example.synoptrack.core.presentation.components.SynopTrackButton
import com.example.synoptrack.social.domain.model.FriendRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequestsScreen(
    onBack: () -> Unit,
    onProfileClick: (String) -> Unit,
    viewModel: FriendRequestsViewModel = hiltViewModel()
) {
    val requests by viewModel.requests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            com.example.synoptrack.core.presentation.components.SynopTrackTopBar(
                title = "Friend Requests",
                onBack = onBack
            )
        }
    ) { padding ->
        if (requests.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No pending requests", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(requests) { request ->
                    RequestItem(
                        request = request,
                        onAccept = { viewModel.acceptRequest(request.id) },
                        onReject = { viewModel.rejectRequest(request.id) },
                        onProfileClick = { onProfileClick(request.senderId) }
                    )
                }
            }
        }
        
        if (isLoading) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                 CircularProgressIndicator()
             }
        }
    }
}

@Composable
fun RequestItem(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onProfileClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
         AsyncImage(
            model = request.senderAvatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${request.senderDisplayName}" },
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${request.senderDisplayName} sent you a friend request.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SynopTrackButton(
                    text = "Confirm",
                    onClick = onAccept,
                    size = com.example.synoptrack.core.presentation.components.ButtonSize.SMALL,
                    modifier = Modifier.weight(1f),
                    fullWidth = false
                )
                SynopTrackButton(
                    text = "Delete",
                    onClick = onReject,
                    variant = ButtonVariant.OUTLINED,
                    size = com.example.synoptrack.core.presentation.components.ButtonSize.SMALL,
                    modifier = Modifier.weight(1f),
                    fullWidth = false
                )
            }
        }
    }
}
