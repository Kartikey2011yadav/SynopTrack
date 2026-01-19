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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.synoptrack.R
import com.example.synoptrack.core.theme.ElectricBluePrimary
import com.example.synoptrack.core.theme.TextGray

@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel = hiltViewModel(),
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { viewModel.checkPermissions(context) }
    )
    val contactsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { viewModel.checkPermissions(context) }
    )
    
    // Notification launcher checks version internally when used commonly, 
    // but here we might need version check logic inside the onClick or helper.
    // Simplifying for clarity:
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { viewModel.checkPermissions(context) }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { viewModel.checkPermissions(context) }
    )

    LaunchedEffect(key1 = true) {
        viewModel.checkPermissions(context)
    }
    
    // Progress Calculation
    val totalPermissions = 4
    val grantedPermissions = listOf(
        uiState.isLocationGranted, 
        uiState.isContactsGranted, 
        uiState.isNotificationGranted,
        uiState.isCameraGranted
    ).count { it }
    
    val progress = grantedPermissions.toFloat() / totalPermissions

    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            Button(
                onClick = onPermissionGranted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Start Exploring", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
            Spacer(modifier = Modifier.height(20.dp))
            
            // Icon
            Icon(
                imageVector = Icons.Default.LocationOn, 
                contentDescription = null, 
                modifier = Modifier.size(80.dp),
                tint = ElectricBluePrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "You're almost ready!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "To give you the full experience, SynopTrack needs a few permissions.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = ElectricBluePrimary,
                trackColor = Color(0xFF1E1E1E),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$grantedPermissions of $totalPermissions unlocked",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Permission Items
            
            // Location
            PermissionItem(
                title = "Location Access",
                description = "To see friends on the map and share your journey.",
                icon = Icons.Default.LocationOn,
                isGranted = uiState.isLocationGranted,
                onClick = {
                    locationLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Contacts
            PermissionItem(
                title = "Contacts Access",
                description = "To find friends and connect with people you know.",
                icon = Icons.Rounded.Person,
                isGranted = uiState.isContactsGranted,
                onClick = {
                    contactsLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Camera
            PermissionItem(
                title = "Camera Access",
                description = "To scan QR codes and share moments.",
                icon = androidx.compose.material.icons.Icons.Default.CameraAlt,
                isGranted = uiState.isCameraGranted,
                onClick = {
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionItem(
                    title = "Notifications",
                    description = "Get alerts when friends start a convoy or message you.",
                    icon = Icons.Default.Notifications,
                    isGranted = uiState.isNotificationGranted,
                    onClick = {
                         notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            } else {
                 // Mock item if below Android 13 to keep UI consistent or hide
                 // Ideally if assumed granted, logic sets it to true.
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
