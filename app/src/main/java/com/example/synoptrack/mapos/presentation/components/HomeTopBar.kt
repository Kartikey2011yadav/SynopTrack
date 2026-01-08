package com.example.synoptrack.mapos.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    onSocialClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background) // Opaque background
            .padding(vertical = 12.dp, horizontal = 16.dp), // Check padding logic in parent
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Add Button
        IconButton(onClick = onAddClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        // Center: App Title (Instagram Style)
        Text(
            text = "SynopTrack",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Right: Social / Notifications
        IconButton(onClick = onSocialClick) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "Social",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
