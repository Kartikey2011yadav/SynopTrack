package com.example.synoptrack.core.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class ButtonVariant {
    PRIMARY,
    OUTLINED,
    TEXT,
    DANGER
}

@Composable
fun SynopTrackButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
    icon: ImageVector? = null
) {
    var commonModifier = modifier.height(50.dp)
    if (fullWidth) {
        commonModifier = commonModifier.fillMaxWidth()
    }

    val shape = RoundedCornerShape(12.dp)

    when (variant) {
        ButtonVariant.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = commonModifier,
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                ButtonContent(text, isLoading, icon)
            }
        }
        ButtonVariant.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = commonModifier,
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                ButtonContent(text, isLoading, icon)
            }
        }
        ButtonVariant.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = commonModifier,
                enabled = enabled && !isLoading,
                shape = shape
            ) {
                ButtonContent(text, isLoading, icon)
            }
        }
        ButtonVariant.DANGER -> {
             Button(
                onClick = onClick,
                modifier = commonModifier,
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                ButtonContent(text, isLoading, icon)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    isLoading: Boolean,
    icon: ImageVector?
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp,
            color = LocalContentColor.current
        )
    } else {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
