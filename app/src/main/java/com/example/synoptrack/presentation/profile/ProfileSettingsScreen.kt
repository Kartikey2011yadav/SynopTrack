package com.example.synoptrack.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ThemeSelectorSection()
            }
            item {
                SettingsItem(icon = Icons.Default.LocationOn, title = "Location Privacy", onClick = {})
            }
            item {
                SettingsItem(icon = Icons.Default.Notifications, title = "Notifications", onClick = {})
            }
            item {
                SettingsItem(icon = Icons.Default.Person, title = "Account", onClick = {})
            }
            item {
                SettingsItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Logout",
                    onClick = onLogoutClick,
                    textColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ThemeSelectorSection() {
    val themes = listOf(
        ThemeOption("System", Color.Gray),
        ThemeOption("Ocean", Color.Blue),
        ThemeOption("Forest", Color.Green),
        ThemeOption("Night", Color.Black)
    )
    var selectedTheme by remember { mutableStateOf(themes.first()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("App Theme", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(themes) { theme ->
                ThemeBubble(
                    theme = theme,
                    isSelected = theme == selectedTheme,
                    onClick = { selectedTheme = theme }
                )
            }
        }
    }
}

@Composable
fun ThemeBubble(theme: ThemeOption, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(theme.color)
                .then(if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(theme.name, style = MaterialTheme.typography.bodySmall)
    }
}

data class ThemeOption(val name: String, val color: Color)

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    ListItem(
        headlineContent = { Text(title, color = textColor) },
        leadingContent = { Icon(icon, contentDescription = null, tint = textColor) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

