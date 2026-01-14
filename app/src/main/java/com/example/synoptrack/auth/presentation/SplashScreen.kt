package com.example.synoptrack.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.synoptrack.core.navigation.Screen

@Composable
fun SplashScreen(
    onNavigate: (String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        kotlinx.coroutines.delay(2000) // Increase splash duration
        // Wait for destination to be determined if delay finishes first
    }

    LaunchedEffect(destination) {
        if (destination != null) {
            // Ensure strict 2s delay passed? Or parallel?
            // Simple approach: Nested LaunchedEffect for delay might check condition.
            // Better: Combine delay and destination check.
            kotlinx.coroutines.delay(2000)

            val dest = destination!!
            if (dest == Screen.Home.route) {
                val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    onNavigate(Screen.Home.route)
                } else {
                    onNavigate(Screen.Permission.route)
                }
            } else {
                onNavigate(dest)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Dark background
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.synoptrack.R.mipmap.ic_launcher_round),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SynopTrack",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                color = com.example.synoptrack.core.theme.ElectricBluePrimary
            )
        }
    }
}

