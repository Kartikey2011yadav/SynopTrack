package com.example.synoptrack.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.synoptrack.R
import com.example.synoptrack.core.presentation.components.SynopTrackButton
import com.example.synoptrack.core.presentation.components.SynopTrackTextField
import com.example.synoptrack.core.utils.IdentityUtils
import kotlinx.coroutines.delay

@Composable
fun NameSetupScreen(
    onSetupComplete: (String, String) -> Unit,
    checkAvailability: suspend (String, String) -> Boolean,
    isSubmitting: Boolean = false
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
            delay(500) // Debounce
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
    
    val isValid = username.length >= 3 && discriminator.length == 4 && !isDiscriminatorTaken && !isChecking && errorMessage == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ... (Header)
        Image(
            painter = painterResource(id = R.drawable.social_hashtag),
            contentDescription = null,
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Create Your Identity",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SynopTrackTextField(
            value = username,
            onValueChange = { 
                username = it 
                if (it.any { char -> !char.isLetterOrDigit() && char != '_' }) {
                    errorMessage = "Only letters, numbers and _ allowed"
                } else if (username.length < 3) {
                    errorMessage = null
                } else {
                    errorMessage = null
                }
            },
            label = "User Name",
            error = if (username.isNotEmpty() && username.any { char -> !char.isLetterOrDigit() && char != '_' }) errorMessage else null,
            isSuccess = isValid,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            SynopTrackTextField(
                value = discriminator,
                onValueChange = { if (it.length <= 4) discriminator = it },
                label = "Tag",
                modifier = Modifier.width(140.dp),
                leadingIcon = { Text("#", modifier = Modifier.padding(start = 12.dp)) },
                error = if (isDiscriminatorTaken) "Taken" else null
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { discriminator = IdentityUtils.generateDiscriminator() }) {
                Text("↻", style = MaterialTheme.typography.headlineSmall) 
            }
        }
        
        if (isDiscriminatorTaken) {
             Text(
                text = "User with same discriminator exist choose another", 
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SynopTrackButton(
            text = "Next",
            onClick = { onSetupComplete(username, discriminator) },
            enabled = isValid && !isSubmitting,
            isLoading = isChecking || isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun NameSetupScreenPreview() {
    NameSetupScreen(
        onSetupComplete = { _, _ -> },
        checkAvailability = { _, _ -> true }
    )
}
