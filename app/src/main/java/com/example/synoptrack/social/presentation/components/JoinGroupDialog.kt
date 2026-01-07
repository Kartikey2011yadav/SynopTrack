package com.example.synoptrack.social.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun JoinGroupDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Join a Group") },
        text = {
            Column {
                Text("Enter the 6-character invite code:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = { if (it.length <= 6) inviteCode = it.uppercase() },
                    singleLine = true,
                    label = { Text("Invite Code") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onJoin(inviteCode) },
                enabled = inviteCode.length == 6
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
