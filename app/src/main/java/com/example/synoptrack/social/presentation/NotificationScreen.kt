package com.example.synoptrack.social.presentation

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
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
import coil.compose.AsyncImage
import com.example.synoptrack.core.presentation.components.ButtonVariant
import com.example.synoptrack.core.presentation.components.SynopTrackButton
import com.example.synoptrack.profile.domain.model.NotificationEntity
import com.example.synoptrack.profile.domain.model.NotificationType
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.Date
import kotlin.collections.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    onRequestClick: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val requestCount by viewModel.pendingRequestCount.collectAsState()
    
    // Group Notifications
    val (today, yesterday, last7Days, older) = remember(notifications) {
        groupNotifications(notifications)
    }

    Scaffold(
        topBar = {
            com.example.synoptrack.core.presentation.components.SynopTrackTopBar(
                title = "Notifications",
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header for Follow Requests
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onRequestClick() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                         contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (requestCount > 99) "99+" else requestCount.toString(), 
                            color = MaterialTheme.colorScheme.onPrimaryContainer, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Follow requests", style = MaterialTheme.typography.titleSmall)
                        Text("Approve or ignore requests", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                     Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                }
            }
            
            if (today.isNotEmpty()) {
                item { SectionHeader("Today") }
                items(today) { NotificationItem(it, viewModel) }
            }
            if (yesterday.isNotEmpty()) {
                item { SectionHeader("Yesterday") }
                 items(yesterday) { NotificationItem(it, viewModel) }
            }
             if (last7Days.isNotEmpty()) {
                item { SectionHeader("Last 7 days") }
                 items(last7Days) { NotificationItem(it, viewModel) }
            }
             if (older.isNotEmpty()) {
                item { SectionHeader("Older") }
                 items(older) { NotificationItem(it, viewModel) }
            }
            
            if (notifications.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No notifications yet", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title, 
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun NotificationItem(
    notification: NotificationEntity,
    viewModel: NotificationViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
         AsyncImage(
            model = notification.senderAvatar.ifEmpty { "https://ui-avatars.com/api/?name=${notification.senderName}" },
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(
                    text = notification.senderName, 
                    style = MaterialTheme.typography.bodyMedium, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
             }
             
             // Time
             val timeAgo = DateUtils.getRelativeTimeSpanString(notification.timestamp.toDate().time)
             Text(text = timeAgo.toString(), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
             
             // Action Buttons for Friend Requests
             if (notification.type == NotificationType.FRIEND_REQUEST) {
                 Spacer(modifier = Modifier.height(8.dp))
                 Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                     SynopTrackButton(
                         text = "Confirm",
                         onClick = { viewModel.acceptRequest(notification) },
                         size = com.example.synoptrack.core.presentation.components.ButtonSize.SMALL,
                         modifier = Modifier.weight(1f),
                         fullWidth = false
                     )
                     SynopTrackButton(
                         text = "Delete",
                         onClick = { viewModel.rejectRequest(notification) },
                         variant = ButtonVariant.OUTLINED,
                         size = com.example.synoptrack.core.presentation.components.ButtonSize.SMALL,
                         modifier = Modifier.weight(1f),
                         fullWidth = false
                     )
                 }
             }
        }
    }
}

// Grouping Logic
data class NotificationGroups(
    val today: List<NotificationEntity>,
    val yesterday: List<NotificationEntity>,
    val last7Days: List<NotificationEntity>,
    val older: List<NotificationEntity>
)

fun groupNotifications(list: List<NotificationEntity>): NotificationGroups {
    val now = Calendar.getInstance()
    val todayStart = now.apply { 
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) 
    }.timeInMillis
    
    val yesterdayStart = todayStart - 86400000
    val last7DaysStart: Long = todayStart - (7 * 86400000)
    
    val today = list.filter { it.timestamp.toDate().time >= todayStart }
    val yesterday = list.filter { it.timestamp.toDate().time in yesterdayStart until todayStart }
    val last7 = list.filter { it.timestamp.toDate().time in last7DaysStart until yesterdayStart }
    val older = list.filter { it.timestamp.toDate().time < last7DaysStart }
    
    return NotificationGroups(today, yesterday, last7, older)
}
