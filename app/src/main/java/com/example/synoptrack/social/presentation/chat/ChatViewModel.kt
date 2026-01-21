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

    private val _seenBy = MutableStateFlow<List<String>>(emptyList())
    val seenBy: StateFlow<List<String>> = _seenBy.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    val currentUser = authRepository.currentUser

    init {
        if (groupId.isNotEmpty()) {
            loadData()
        }
    }

    private fun loadData() {
        // 1. Observe Messages
        viewModelScope.launch {
            chatRepository.getMessages(groupId).collectLatest { msgs ->
                // Mark as read if last message is from other
                if (msgs.isNotEmpty()) {
                    val lastMsg = msgs.first() // Assumes descending order from repo
                    if (lastMsg.senderId != currentUser?.uid) {
                        launch { chatRepository.markAsRead(groupId, currentUser?.uid ?: "") }
                    }
                }
                _messages.value = msgs.asReversed() // LazyColumn reverseLayout=true needs newest at index 0? 
                // Wait, if reverseLayout=true, item 0 is at bottom.
                // If repo returns DESCENDING (Newest first), then item 0 is newest.
                // So passed to LazyColumn, item 0 (newest) is at bottom. Correct.
                // But `asReversed` was used before?
                // `_messages.value = it.asReversed()`
                // If Repo returns [Newest, UsedToBeNewest, ..., Oldest]
                // `asReversed` -> [Oldest, ..., Newest]
                // LazyColumn reverseLayout -> Item 0 (Oldest) at bottom? NO.
                // Item 0 is at Bottom in reverseLayout.
                // We want NEWEST at bottom.
                // So we want list to be [Newest, ..., Oldest] if item 0 is bottom.
                // So if Repo returns [Newest, ...], we should use it AS IS?
                // Let's re-read ChatScreen: `items(messages)` inside `LazyColumn(reverseLayout = true)`.
                // In reverse layout, the first item in the list is drawn at the bottom.
                // So `messages[0]` is at bottom.
                // We want Newest at bottom.
                // So `messages[0]` should be Newest.
                // Repo returns DESCENDING by timestamp (Newest first).
                // So `it[0]` is Newest.
                // So `_messages.value = it` should be correct.
                // Previous code: `_messages.value = it.asReversed()`
                // That would mean Oldest at bottom. That seems WRONG for a chat app unless Repo returned Ascending.
                // ChatRepositoryImpl: `.orderBy("timestamp", Query.Direction.DESCENDING)`
                // So Repo returns Newest First.
                // If I used `asReversed`, I reversed it to Oldest First.
                // So Oldest was at bottom.
                // That means I was scrolling UP to see newer messages?
                // Or maybe `reverseLayout=true` puts item 0 at the bottom of the visible area?
                // Yes.
                // So usually chat apps: Newest at bottom.
                // So `messages[0]` should be Newest.
                // So I definitely should NOT use `asReversed` if Repo is Descending.
                // I'll trust my analysis and remove `asReversed`.
                _messages.value = msgs
            }
        }
        
        // 2. Observe Conversation (for SeenBy)
        viewModelScope.launch {
            chatRepository.getConversation(groupId).collectLatest { conversation ->
                conversation?.let {
                    // Map SeenBy UIDs to Avatar URLs (excluding current user)
                    val otherSeenParticipants = it.seenBy.filter { uid -> uid != currentUser?.uid }
                    val avatars = otherSeenParticipants.mapNotNull { uid ->
                        it.participantData[uid]?.avatarUrl
                    }
                    _seenBy.value = avatars
                }
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
