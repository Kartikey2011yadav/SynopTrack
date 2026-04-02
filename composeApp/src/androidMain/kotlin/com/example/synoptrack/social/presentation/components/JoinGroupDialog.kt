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
                SynopTrackTextField(
                    value = inviteCode,
                    onValueChange = { if (it.length <= 6) inviteCode = it.uppercase() },
                    label = "Invite Code",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            SynopTrackButton(
                text = "Join",
                onClick = { onJoin(inviteCode) },
                enabled = inviteCode.length == 6,
                fullWidth = false,
                modifier = Modifier.width(100.dp)
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
