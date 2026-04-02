package com.example.synoptrack.core.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SynopTrackTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String = "",
    error: String? = null,
    isSuccess: Boolean = false,
    secure: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isError = error != null

    val successColor = Color(0xFF4CAF50)
    val errorColor = MaterialTheme.colorScheme.error
    val primaryColor = MaterialTheme.colorScheme.primary

    // Determine active colors based on state
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> errorColor
            isSuccess -> successColor
            else -> MaterialTheme.colorScheme.outline
        }, label = "borderColor"
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            visualTransformation = if (secure && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            shape = RoundedCornerShape(12.dp),
            leadingIcon = leadingIcon,
            interactionSource = interactionSource,
            trailingIcon = {
                if (secure) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                } else if (isError) {
                    Icon(Icons.Filled.Error, "Error", tint = errorColor)
                } else if (isSuccess) {
                    Icon(Icons.Filled.Check, "Valid", tint = successColor)
                } else {
                    trailingIcon?.invoke()
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isSuccess) successColor else primaryColor,
                unfocusedBorderColor = if (isSuccess) successColor else MaterialTheme.colorScheme.outline,
                errorBorderColor = errorColor,
                focusedLabelColor = if (isSuccess) successColor else primaryColor,
                errorLabelColor = errorColor,
                cursorColor = if (isSuccess) successColor else primaryColor
            )
        )
        
        if (isError) {
            Text(
                text = error ?: "",
                color = errorColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
