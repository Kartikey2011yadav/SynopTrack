package com.example.synoptrack.social.presentation.invite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun InviteHandlerScreen(
    code: String?,
    onDismiss: () -> Unit,
    viewModel: InviteViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val requestSent by viewModel.requestSent.collectAsState()

    LaunchedEffect(code) {
        if (code != null) {
            viewModel.loadUserByCode(code)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (user != null) {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Friend Invite") },
                text = {
                    if (requestSent) {
                        Text("Friend request sent to ${user!!.username}!")
                    } else {
                        Text("Do you want to add ${user!!.username}#${user!!.discriminator} as a friend?")
                    }
                },
                confirmButton = {
                    if (!requestSent) {
                        Button(onClick = { viewModel.sendFriendRequest(user!!.uid) }) {
                            Text("Add Friend")
                        }
                    } else {
                        Button(onClick = onDismiss) {
                            Text("Done")
                        }
                    }
                },
                dismissButton = {
                    if (!requestSent) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                    }
                }
            )
        } else if (error != null) {
             AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Error") },
                text = { Text(error ?: "Unknown error") },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
