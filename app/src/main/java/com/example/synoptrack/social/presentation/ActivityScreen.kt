package com.example.synoptrack.social.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.synoptrack.social.domain.model.FriendRequest

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val pendingRequests by viewModel.pendingRequests.collectAsState()

    ActivityScreenContent(
        pendingRequests = pendingRequests,
        onAccept = { requestId -> viewModel.acceptRequest(requestId) },
        onReject = { requestId -> viewModel.rejectRequest(requestId) }
    )
}

@Composable
fun ActivityScreenContent(
    pendingRequests: List<FriendRequest>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            "Notifications",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (pendingRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No new notifications", color = androidx.compose.ui.graphics.Color.Gray)
            }
        } else {
             Text(
                "Friend Requests",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
             androidx.compose.foundation.lazy.LazyColumn {
                 items(pendingRequests) { request ->
                     FriendRequestItem(
                         request = request,
                         onAccept = { onAccept(request.id) },
                         onReject = { onReject(request.id) }
                     )
                 }
             }
        }
    }
}

@Composable
fun FriendRequestItem(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                 Text(
                     text = request.senderDisplayName,
                     style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                 )
                 Text("wants to be friends", style = MaterialTheme.typography.bodySmall)
             }
             
             androidx.compose.material3.IconButton(onClick = onReject) {
                 androidx.compose.material3.Icon(
                     imageVector = androidx.compose.material.icons.Icons.Default.Close,
                     contentDescription = "Reject",
                     tint = androidx.compose.ui.graphics.Color.Red
                 )
             }
             androidx.compose.material3.IconButton(onClick = onAccept) {
                 androidx.compose.material3.Icon(
                     imageVector = androidx.compose.material.icons.Icons.Default.Check,
                     contentDescription = "Accept",
                     tint = androidx.compose.ui.graphics.Color.Green
                 )
             }
        }
    }
}

@Preview
@Composable
fun ActivityScreenPreview() {
    val mockRequests = listOf(
        FriendRequest(id = "1", senderDisplayName = "Alice"),
        FriendRequest(id = "2", senderDisplayName = "Bob")
    )
    ActivityScreenContent(
        pendingRequests = mockRequests,
        onAccept = {},
        onReject = {}
    )
}
