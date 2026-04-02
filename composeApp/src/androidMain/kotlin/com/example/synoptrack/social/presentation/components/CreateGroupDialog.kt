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
fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Group") },
        text = {
            Column {
                Text("Give your convoy a name:")
                Spacer(modifier = Modifier.height(8.dp))
                SynopTrackTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = "Group Name",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            SynopTrackButton(
                text = "Create",
                onClick = { onCreate(groupName) },
                enabled = groupName.isNotBlank(),
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
