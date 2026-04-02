package com.example.synoptrack.core.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning

enum class ToastVariant(
    val backgroundColor: Color,
    val icon: ImageVector
) {
    SUCCESS(Color(0xFF43A047), Icons.Default.CheckCircle),
    ERROR(Color(0xFFE53935), Icons.Default.Error),
    WARNING(Color(0xFFFFB300), Icons.Default.Warning),
    INFO(Color(0xFF1E88E5), Icons.Default.Info),
    PRIMARY(Color(0xFF1976D2), Icons.Default.Info)
}

data class ToastMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val variant: ToastVariant = ToastVariant.INFO,
    val duration: Long = 4000L
)
