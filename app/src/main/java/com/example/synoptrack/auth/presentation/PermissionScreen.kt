package com.example.synoptrack.auth.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.synoptrack.R
import com.example.synoptrack.core.theme.ElectricBluePrimary
import com.example.synoptrack.core.theme.TextGray
import kotlinx.coroutines.launch

@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel = hiltViewModel(),
    onPermissionGranted: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigation Effects
    LaunchedEffect(uiState.isPermissionGranted) {
        if (uiState.isPermissionGranted) onPermissionGranted()
    }

    LaunchedEffect(uiState.isSkipped) {
        if (uiState.isSkipped) onSkip()
    }

    // Rationale SnackBar
    LaunchedEffect(uiState.showRationale, uiState.errorMessage) {
        if (uiState.showRationale && uiState.errorMessage != null) {
            snackbarHostState.showSnackbar(
                message = uiState.errorMessage!!,
                actionLabel = "Retry",
                duration = SnackbarDuration.Short
            )
            viewModel.resetRationale()
        }
    }

    val permissionsToRequest = remember {
        mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                              permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (locationGranted) {
            viewModel.onPermissionGranted()
        } else {
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION)
            } ?: false
            viewModel.onPermissionDenied(shouldShowRationale)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Illustration
                Surface(
                    modifier = Modifier.size(160.dp),
                    shape = CircleShape,
                    color = ElectricBluePrimary.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Permission",
                        modifier = Modifier
                            .padding(40.dp)
                            .fillMaxSize(),
                        tint = ElectricBluePrimary
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 2. Title
                Text(
                    text = "Allow Maps Access",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Body
                Text(
                    text = "SynopTrack uses your location to show friends nearby and create a live social map experience.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextGray
                    ),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            // 4. Action Buttons (Bottom)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = {
                        if (uiState.isPermanentlyDenied) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        } else {
                            permissionLauncher.launch(permissionsToRequest)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBluePrimary
                    )
                ) {
                    Text(
                        text = if (uiState.isPermanentlyDenied) "Open Settings" else "Allow Access",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { viewModel.onSkipForNow() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Skip for Now",
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
