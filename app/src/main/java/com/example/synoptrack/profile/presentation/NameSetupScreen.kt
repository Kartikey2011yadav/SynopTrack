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
    onSetupComplete: (String, String) -> Unit,
    checkAvailability: suspend (String, String) -> Boolean
) {
    var username by remember { mutableStateOf("") }
    var discriminator by remember { mutableStateOf(IdentityUtils.generateDiscriminator()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDiscriminatorTaken by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }
    
    // Debounced Validation
    LaunchedEffect(username, discriminator) {
        if (username.length >= 3 && discriminator.length == 4) {
            isChecking = true
            kotlinx.coroutines.delay(500) // Debounce
            val available = checkAvailability(username, discriminator)
            if (!available) {
                isDiscriminatorTaken = true
                errorMessage = "User with same discriminator exist choose another"
            } else {
                isDiscriminatorTaken = false
                errorMessage = null
            }
            isChecking = false
        } else {
            isDiscriminatorTaken = false
            errorMessage = null 
        }
    }
    
    val isValid = username.length >= 3 && discriminator.length == 4 && !isDiscriminatorTaken && !isChecking

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ... (Header)
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.synoptrack.R.drawable.social_hashtag),
            contentDescription = null,
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth(),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Create Your Identity",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = username,
            onValueChange = { 
                username = it 
                if (it.any { char -> !char.isLetterOrDigit() && char != '_' }) {
                    errorMessage = "Only letters, numbers and _ allowed"
                }
            },
            label = { Text("User Name") },
            isError = errorMessage != null && !isDiscriminatorTaken, // Only show username error if not discriminator error
            modifier = Modifier.fillMaxWidth()
        )
        // ...
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = discriminator,
                onValueChange = { if (it.length <= 4) discriminator = it },
                label = { Text("Tagline") },
                modifier = Modifier.width(140.dp),
                prefix = { Text("#") },
                isError = isDiscriminatorTaken,
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { discriminator = IdentityUtils.generateDiscriminator() }) {
                Text("↻") // Randomize button
            }
        }
        
        if (isDiscriminatorTaken) {
             Text(
                text = "User with same discriminator exist choose another", 
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else if (errorMessage != null) {
              Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { onSetupComplete(username, discriminator) },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isChecking) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Next")
            }
        }
    }
}
