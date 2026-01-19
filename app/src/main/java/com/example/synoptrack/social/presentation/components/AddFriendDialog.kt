package com.example.synoptrack.social.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddFriendDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Friend") },
        text = {
            Column {
                Text("Enter their Invite Code:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    singleLine = true,
                    placeholder = { Text("e.g. user#1234@abcd") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (code.length > 5) onAdd(code) },
                enabled = code.length > 5
            ) {
                Text("Add Friend")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
