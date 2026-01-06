package com.example.synoptrack.mapos.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

data class DiscoveryItem(
    val title: String,
    val subtitle: String,
    val rating: Double,
    val distance: String,
    val imageUrl: String // Placeholder color or url
)

@Composable
fun DiscoveryOverlay(modifier: Modifier = Modifier) {
    val items = listOf(
        DiscoveryItem("Air Cafe", "Fine Dining • $$$", 5.0, "2km Away", "https://placeholder"),
        DiscoveryItem("Bluebird Cafe", "Casual • $$", 4.8, "5km Away", "https://placeholder"),
        DiscoveryItem("City View", "Bar • $$$$", 4.9, "1km Away", "https://placeholder")
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            DiscoveryCard(item)
        }
    }
}

@Composable
fun DiscoveryCard(item: DiscoveryItem) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f) // Glass-like background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ideally an image here
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Gray.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                         Text(item.title.take(1), fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                           Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(12.dp))
                           Spacer(modifier = Modifier.width(4.dp))
                           Text("${item.rating} • ${item.subtitle}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.distance,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
            }

            // Like Button
            IconButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Like", tint = Color.White)
            }
        }
    }
}
