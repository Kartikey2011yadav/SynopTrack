package com.example.synoptrack.auth.presentation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import com.example.synoptrack.R
import com.example.synoptrack.core.theme.ElectricBluePrimary
import com.example.synoptrack.core.theme.TextGray

@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel = hiltViewModel(),
    onPermissionGranted: () -> Unit // When all required are granted
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    // Check if critical permissions are granted (Location)
    // If we want to force location, we check it here. Or we let the user proceed if they skip.
    // For this refactor, let's assume we want at least Location or explicitly skipped.
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { 
            // Update ViewModel state 
            viewModel.checkPermissions(context)
        }
    )

    LaunchedEffect(key1 = true) {
        viewModel.checkPermissions(context)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Button(
                onClick = onPermissionGranted, // Or skip/finish
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Start Exploring", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Header Image (Cloud/Lock/Check)
            Icon(
                imageVector = Icons.Default.LocationOn, // Placeholder
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = ElectricBluePrimary
            )
            
            Text(
                text = "You're almost ready!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "To give you the full experience, SynopTrack needs a few permissions.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Permission Items
            PermissionItem(
                title = "Location Access",
                description = "To see friends on the map and share your journey.",
                icon = Icons.Default.LocationOn,
                isGranted = uiState.isLocationGranted,
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Notification Item (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notifLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { viewModel.checkPermissions(context) }
                )
                
                PermissionItem(
                    title = "Notifications",
                    description = "Get alerts when friends start a convoy or message you.",
                    icon = Icons.Default.Notifications,
                    isGranted = uiState.isNotificationGranted, // Need to add to State
                    onClick = {
                        notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            }
        }
    }
}

@Composable
fun PermissionItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) ElectricBluePrimary else TextGray, // Or different color
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = TextGray)
            }
            
            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Granted",
                    tint = ElectricBluePrimary
                )
            }
        }
    }
}
