package com.example.synoptrack.profile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.synoptrack.core.utils.IdentityUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    currentName: String,
    currentDiscriminator: String,
    currentBio: String,
    onBackClick: () -> Unit,
    onSaveClick: (String, String, String) -> Unit, // name, discriminator, bio
    checkAvailability: suspend (String, String) -> Boolean
) {
    var name by remember { mutableStateOf(currentName) }
    var discriminator by remember { mutableStateOf(currentDiscriminator) }
    var bio by remember { mutableStateOf(currentBio) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDiscriminatorTaken by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (discriminator.isEmpty()) {
            discriminator = IdentityUtils.generateDiscriminator()
        }
    }
    
    // validation
    LaunchedEffect(name, discriminator) {
        if (name.length >= 3 && discriminator.length == 4) {
            // Check only if changed from initial
            if (name != currentName || discriminator != currentDiscriminator) {
                isChecking = true
                kotlinx.coroutines.delay(500)
                val available = checkAvailability(name, discriminator)
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
    }
    
    val isValid = name.isNotEmpty() && discriminator.length == 4 && !isDiscriminatorTaken && !isChecking

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (isValid) {
                            onSaveClick(name, discriminator, bio)
                        } else {
                            if (discriminator.length != 4) errorMessage = "Hash must be 4 characters"
                        }
                    }, enabled = isValid) {
                         if (isChecking) {
                             androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(24.dp))
                         } else {
                             Icon(Icons.Default.Check, contentDescription = "Save")
                         }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            
            // Identity Row
            Text("Identity", style = MaterialTheme.typography.labelMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("User Name") },
                    modifier = Modifier.weight(1f),
                    isError = errorMessage != null && !isDiscriminatorTaken
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                OutlinedTextField(
                    value = discriminator,
                    onValueChange = { if (it.length <= 4) discriminator = it },
                    label = { Text("Tagline") },
                    modifier = Modifier.width(140.dp),
                    prefix = { Text("#") },
                    isError = isDiscriminatorTaken
                )
            }
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bio
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )
        }
    }
}

@Preview
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(
        currentName = "John Doe",
        currentDiscriminator = "1234",
        currentBio = "Hello world",
        onBackClick = {},
        onSaveClick = { _, _, _ -> },
        checkAvailability = { _, _ -> true }
    )
}
