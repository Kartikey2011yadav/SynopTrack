package com.example.synoptrack.social.presentation.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.social.domain.model.Message
import com.example.synoptrack.social.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId: String = savedStateHandle.get<String>("groupId") ?: ""

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    val currentUser = authRepository.currentUser

    init {
        if (groupId.isNotEmpty()) {
            loadMessages()
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            chatRepository.getMessages(groupId).collectLatest {
                _messages.value = it.asReversed() // Show newest at bottom usually, but depends on LazyColumn 'reverseLayout'
            }
        }
    }

    fun onMessageChange(text: String) {
        _messageText.value = text
    }

    fun sendMessage() {
        val content = _messageText.value.trim()
        val user = currentUser ?: return
        if (content.isEmpty()) return

        val message = Message(
            senderId = user.uid,
            senderName = user.displayName ?: "User",
            content = content,
            timestamp = null // Let server set it
        )

        viewModelScope.launch {
            _messageText.value = "" // Clear immediately
            chatRepository.sendMessage(groupId, message)
        }
    }
}
