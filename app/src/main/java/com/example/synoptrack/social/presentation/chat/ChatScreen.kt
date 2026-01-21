package com.example.synoptrack.social.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.outlined.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.synoptrack.social.domain.model.Message
import java.text.SimpleDateFormat
import java.util.Locale

import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    val seenAvatars by viewModel.seenBy.collectAsState()
    val chatTitle by viewModel.chatTitle.collectAsState()
    val chatAvatar by viewModel.chatAvatar.collectAsState()
    val currentUser = viewModel.currentUser

    ChatScreenContent(
        messages = messages,
        messageText = messageText,
        seenAvatars = seenAvatars,
        chatTitle = chatTitle,
        chatAvatar = chatAvatar,
        currentUserId = currentUser?.uid,
        onBack = { navController.popBackStack() },
        onMessageChange = { viewModel.onMessageChange(it) },
        onSendMessage = { viewModel.sendMessage() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(
    messages: List<Message>,
    messageText: String,
    seenAvatars: List<String>,
    chatTitle: String,
    chatAvatar: String,
    currentUserId: String?,
    onBack: () -> Unit,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    
    // Theme Colors (Mock)
    val themeColors = listOf(
        Color(0xFF009688), Color(0xFF8BC34A), Color(0xFF2196F3), Color(0xFF673AB7),
        Color(0xFFE91E63), Color(0xFFFF9800), Color(0xFF795548), Color(0xFF607D8B)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                     Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    ) {
                         AsyncImage(
                             model = chatAvatar.ifEmpty { "https://ui-avatars.com/api/?name=$chatTitle" },
                             contentDescription = null,
                             modifier = Modifier.fillMaxSize(),
                             contentScale = ContentScale.Crop
                         )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(chatTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back")
                }
            },
            actions = {
                // Theme Picker
                IconButton(onClick = { showThemePicker = !showThemePicker }) {
                    Icon(Icons.Outlined.Palette, contentDescription = "Theme")
                }
                
                // Theme Dropdown/Grid (simplified as Dropdown for now)
                DropdownMenu(
                    expanded = showThemePicker,
                    onDismissRequest = { showThemePicker = false },
                    modifier = Modifier.width(220.dp).background(MaterialTheme.colorScheme.surface)
                ) {
                     Text("Choose Theme", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.labelLarge)
                     // Grid layout inside column
                     val rows = themeColors.chunked(4)
                     Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                         rows.forEach { rowColors ->
                             Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                 rowColors.forEach { color ->
                                     Box(
                                         modifier = Modifier
                                             .size(40.dp)
                                             .padding(4.dp)
                                             .clip(RoundedCornerShape(8.dp))
                                             .background(color)
                                             .clickable { showThemePicker = false }
                                     )
                                 }
                             }
                         }
                     }
                     Spacer(modifier = Modifier.height(8.dp))
                }

                // More Menu
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface) // Ensure contrast
                ) {
                    DropdownMenuItem(
                        text = { Text("Audio Call") },
                        onClick = { showMenu = false },
                        leadingIcon = { Icon(Icons.Outlined.Call, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Video Call") },
                        onClick = { showMenu = false },
                        leadingIcon = { Icon(Icons.Outlined.VideoCall, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Report") },
                        onClick = { showMenu = false },
                        leadingIcon = { Icon(Icons.Outlined.Report, contentDescription = null) }
                    )
                     DropdownMenuItem(
                        text = { Text("Block", color = Color.Red) },
                        onClick = { showMenu = false },
                        leadingIcon = { Icon(Icons.Outlined.Block, contentDescription = null, tint = Color.Red) }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            reverseLayout = true // Start from bottom
        ) {
            items(messages.size) { index ->
                val message = messages[index]
                val isMe = message.senderId == currentUserId
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    MessageBubble(message = message, isMe = isMe)
                    
                    // Seen Indicator (Only for newest message sent by Me)
                    if (index == 0 && isMe && seenAvatars.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                             horizontalArrangement = Arrangement.spacedBy((-8).dp) // Overlap slightly implies grouping
                        ) {
                            seenAvatars.forEach { url ->
                                androidx.compose.foundation.Image(
                                     painter = coil.compose.rememberAsyncImagePainter(url.ifEmpty { "https://ui-avatars.com/api/?name=User" }),
                                     contentDescription = null,
                                     modifier = Modifier
                                         .size(16.dp)
                                         .clip(CircleShape)
                                         .background(Color.Gray)
                                         .border(1.dp, MaterialTheme.colorScheme.background, CircleShape),
                                     contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Input Area
        Column(modifier = Modifier.fillMaxWidth().paddingFromBaseline(bottom = 42.dp)) {
            // Attachments (Hidden by default, shown on Plus click? Or inline?)
            // Image shows them in a small floating card ABOVE the input bar usually.
            // Let's keep it simple: Expandable Row inside the bar.
            var showAttachments by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Plus / Actions Button
                IconButton(
                    onClick = { showAttachments = !showAttachments },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        if (showAttachments) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Attachments",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Attachments Row (Animated)
                androidx.compose.animation.AnimatedVisibility(
                    visible = showAttachments,
                    enter = androidx.compose.animation.expandHorizontally(),
                    exit = androidx.compose.animation.shrinkHorizontally()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { }) { Icon(Icons.Outlined.CameraAlt, "Camera") }
                        IconButton(onClick = { }) { Icon(Icons.Outlined.Image, "Gallery") }
                        IconButton(onClick = { }) { Icon(Icons.Outlined.EmojiEmotions, "Emoji") }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Input
                TextField(
                    value = messageText,
                    onValueChange = onMessageChange,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp), // Fixed height for alignment
                    placeholder = { Text("Message...", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f),
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(28.dp),
                    singleLine = true // Keep it single line like design usually
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Send Button
                val isSendEnabled = messageText.isNotBlank()
                Button(
                    onClick = onSendMessage,
                    enabled = isSendEnabled,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(48.dp), // Match input roughly
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
                    // Or Icon? Design implies TEXT "SEND" or Icon. 
                    // Let's use clean "SEND" text or Icon. The provided image shows "SEND" text in one variant, or arrow.
                    // I'll stick to Text "SEND" as it's clear.
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        if (!isMe) {
            Text(
                text = message.senderName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isMe) 20.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 20.dp
                    )
                )
                .background(
                    if (isMe) {
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    } else {
                        androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.surfaceVariant)
                    }
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    val mockMessages = listOf(
        Message(id = "1", senderId = "me", senderName = "Me", content = "Hello!"),
        Message(id = "2", senderId = "other", senderName = "Alice", content = "Hi there!")
    )
    ChatScreenContent(
        messages = mockMessages,
        messageText = "",
        seenAvatars = emptyList(),
        chatTitle = "Brooklyn Simmons",
        chatAvatar = "",
        currentUserId = "me",
        onBack = {},
        onMessageChange = {},
        onSendMessage = {}
    )
}
