package com.example.synoptrack.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.synoptrack.core.utils.IdentityUtils

@Composable
fun NameSetupScreen(
    onSetupComplete: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var discriminator by remember { mutableStateOf(IdentityUtils.generateDiscriminator()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val isValid = username.length >= 3 && discriminator.length == 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Your Identity",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = username,
            onValueChange = { 
                username = it 
                // Basic validation (alphanumeric, etc - simplistic for now)
                if (it.any { char -> !char.isLetterOrDigit() && char != '_' }) {
                    errorMessage = "Only letters, numbers and _ allowed"
                } else {
                    errorMessage = null
                }
            },
            label = { Text("Username") },
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("#", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = discriminator,
                onValueChange = { if (it.length <= 4) discriminator = it },
                label = { Text("Discriminator") },
                modifier = Modifier.width(120.dp)
            )
            IconButton(onClick = { discriminator = IdentityUtils.generateDiscriminator() }) {
                Text("↻") // Randomize button
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { onSetupComplete(username, discriminator) },
            enabled = isValid && errorMessage == null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}
