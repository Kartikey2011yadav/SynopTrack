package com.example.synoptrack.social.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.synoptrack.core.presentation.components.ButtonVariant
import com.example.synoptrack.core.presentation.components.SynopTrackButton
import com.example.synoptrack.core.presentation.components.SynopTrackTextField

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
                SynopTrackTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = "Invite Code", // Changed from placeholder to label for consistency, or keep standard logic
                    placeholder = "e.g. user#1234@abcd",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            SynopTrackButton(
                text = "Add Friend",
                onClick = { if (code.length > 5) onAdd(code) },
                enabled = code.length > 5,
                fullWidth = false,
                modifier = Modifier.width(120.dp) // Slightly wider for text
            )
        },
        dismissButton = {
            SynopTrackButton(
                text = "Cancel",
                onClick = onDismiss,
                variant = ButtonVariant.TEXT,
                fullWidth = false,
                modifier = Modifier.width(100.dp)
            )
        }
    )
}
