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

enum class ButtonSize {
    MEDIUM,
    SMALL
}

@Composable
fun SynopTrackButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.MEDIUM,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
    icon: ImageVector? = null
) {
    val height = when (size) {
        ButtonSize.MEDIUM -> 50.dp
        ButtonSize.SMALL -> 32.dp
    }
    
    var commonModifier = modifier.height(height)
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
                ButtonContent(text, isLoading, icon, size)
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
                ),
                contentPadding = if (size == ButtonSize.SMALL) androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp) else ButtonDefaults.ContentPadding
            ) {
                ButtonContent(text, isLoading, icon, size)
            }
        }
        ButtonVariant.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = commonModifier,
                enabled = enabled && !isLoading,
                shape = shape
            ) {
                ButtonContent(text, isLoading, icon, size)
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
                ),
                contentPadding = if (size == ButtonSize.SMALL) androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp) else ButtonDefaults.ContentPadding
            ) {
                ButtonContent(text, isLoading, icon, size)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    isLoading: Boolean,
    icon: ImageVector?,
    size: ButtonSize
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(if (size == ButtonSize.SMALL) 16.dp else 24.dp),
            strokeWidth = 2.dp,
            color = LocalContentColor.current
        )
    } else {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(if (size == ButtonSize.SMALL) 16.dp else 24.dp))
            Spacer(modifier = Modifier.width(if (size == ButtonSize.SMALL) 4.dp else 8.dp))
        }
        Text(
            text = text, 
            style = if (size == ButtonSize.SMALL) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}
