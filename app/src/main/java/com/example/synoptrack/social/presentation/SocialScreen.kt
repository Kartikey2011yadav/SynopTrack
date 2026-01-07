package com.example.synoptrack.social.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.synoptrack.mapos.presentation.MapOSViewModel
import com.example.synoptrack.social.presentation.components.CreateGroupDialog
import com.example.synoptrack.social.presentation.components.JoinGroupDialog

@Composable
fun SocialScreen(
    viewModel: MapOSViewModel = hiltViewModel(),
    onChatClick: (String) -> Unit = {}
) {
    val activeGroup by viewModel.activeGroup.collectAsState()
    val groupMembers by viewModel.groupMembers.collectAsState()
    val isGhostMode by viewModel.isGhostMode.collectAsState()
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp) // Status bar spacing
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Connect", 
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            // Ghost Mode Toggle (Mini)
            Surface(
                onClick = { viewModel.toggleGhostMode() },
                shape = CircleShape,
                color = if (isGhostMode) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(CircleShape)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isGhostMode) "Ghost ON" else "Visible",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isGhostMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Active Convoy Card (Gradient)
        if (activeGroup != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChatClick(activeGroup!!.id) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Active Convoy",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                activeGroup?.name ?: "Unknown Group",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                        ) {
                            Text(
                                activeGroup?.inviteCode ?: "---",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "${groupMembers.size} Members active now",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        } else {
            // Empty State / Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Convoy")
                }
                FilledTonalButton(
                    onClick = { showJoinDialog = true },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Text("Join Code")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recent Activity / Chat Placeholder
        Text(
            "Recent Activity",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mock List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(1) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("S", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("System", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text("Welcome to SynopTrack 2.0!", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateGroupDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                viewModel.createGroup(name)
                showCreateDialog = false
            }
        )
    }

    if (showJoinDialog) {
        JoinGroupDialog(
            onDismiss = { showJoinDialog = false },
            onJoin = { code ->
                viewModel.joinGroup(code)
                showJoinDialog = false
            }
        )
    }
}
