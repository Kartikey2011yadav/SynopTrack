package com.example.synoptrack.social.presentation.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserQrScreen(
    user: com.example.synoptrack.profile.domain.model.UserProfile,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My QR Code") },
                navigationIcon = {
                     IconButton(onClick = onBack) {
                         Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                     }
                }
            )
        }
    ) { padding ->
        Column(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(padding)
                 .padding(24.dp),
             horizontalAlignment = Alignment.CenterHorizontally,
             verticalArrangement = Arrangement.Center
        ) {
             Card(
                 modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                 colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                 shape = RoundedCornerShape(24.dp)
             ) {
                 Column(
                     modifier = Modifier.padding(32.dp),
                     horizontalAlignment = Alignment.CenterHorizontally
                 ) {
                     // Avatar
                     AsyncImage(
                        model = user.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${user.username}" },
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Name and Tag
                    Text(
                        text = user.displayName.ifEmpty { user.username },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "#${user.discriminator}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // QR Code
                    // Using QR Server API for simplicity as used before
                    val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=synoptrack://invite/${user.inviteCode}"
                    
                    AsyncImage(
                        model = qrUrl,
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Invite Code Text
                    Text(
                        text = "Invite Code: ${user.inviteCode}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                 }
             }
             
             Spacer(modifier = Modifier.height(32.dp))
             
             Button(
                 onClick = { /* Share Logic intent */ },
                 modifier = Modifier.fillMaxWidth().height(50.dp)
             ) {
                 Icon(Icons.Default.Share, contentDescription = null)
                 Spacer(modifier = Modifier.width(8.dp))
                 Text("Share Profile")
             }
        }
    }
}
