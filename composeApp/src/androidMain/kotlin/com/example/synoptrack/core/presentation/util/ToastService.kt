package com.example.synoptrack.core.presentation.util

import com.example.synoptrack.core.presentation.model.ToastMessage
import com.example.synoptrack.core.presentation.model.ToastVariant
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToastService @Inject constructor() {
    private val _toastChannel = Channel<ToastMessage>()
    val toastEvents = _toastChannel.receiveAsFlow()

    suspend fun showToast(
        message: String,
        variant: ToastVariant = ToastVariant.INFO,
        duration: Long = 4000L
    ) {
        _toastChannel.send(ToastMessage(message = message, variant = variant, duration = duration))
    }
}
