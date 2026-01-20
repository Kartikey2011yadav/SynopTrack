package com.example.synoptrack.mapos.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synoptrack.core.theme.ErrorRed

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    hasUnseenNotifications: Boolean = false,
    onAddClick: () -> Unit,
    onSocialClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp), // Reduced vertical padding
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
                Box {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (hasUnseenNotifications) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Social",
                            tint = if (hasUnseenNotifications) ErrorRed else MaterialTheme.colorScheme.onBackground
                        )

                        if (hasUnseenNotifications) {
                            Box(
                                modifier = Modifier
//                                    .align(Alignment.TopEnd)
                                    .padding(top = 0.dp,)
                                    .size(8.dp)
                                    .background(ErrorRed, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeTopBarPreview() {
    HomeTopBar(
        onAddClick = {},
        onSocialClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun HomeTopBarWithNotificationPreview() {
    HomeTopBar(
        hasUnseenNotifications = true,
        onAddClick = {},
        onSocialClick = {}
    )
}
