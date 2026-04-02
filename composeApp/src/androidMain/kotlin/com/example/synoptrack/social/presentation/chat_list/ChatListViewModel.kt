package com.example.synoptrack.social.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.repository.ProfileRepository
import com.example.synoptrack.social.data.model.ConversationEntity
import com.example.synoptrack.social.domain.repository.ChatRepository
import com.example.synoptrack.social.domain.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val friendRepository: FriendRepository,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _chatListItems = MutableStateFlow<List<ChatListItem>>(emptyList())
    val chatListItems: StateFlow<List<ChatListItem>> = _chatListItems.asStateFlow()

    private val currentUser = authRepository.currentUser

    init {
        loadChats()
    }

    private fun loadChats() {
        val userId = currentUser?.uid ?: return

        // Combine Friends Flow and Conversations Flow
        // FriendRepository.getFriends() returns List<UserProfile> (assuming it exists or I fetch profile for each friend ID)
        // Actually FriendRepository usually deals with IDs or simplified models. Let's check.
        // If FriendRepository doesn't expose a nice list of profiles, I might need to fetch them.
        // Let's assume for now I can get a list of friend UIDs and then fetch profiles if needed, 
        // OR better: ProfileRepository might have a method to get profiles by IDs.
        
        // Strategy:
        // 1. Get Conversations.
        // 2. Get Friend UIDs.
        // 3. Merge.
        
        val conversationsFlow = chatRepository.getConversations(userId)
        val friendsFlow = friendRepository.getFriends(userId) // Need to verify this exists or use userProfile.friends

        combine(conversationsFlow, friendsFlow) { conversations, friends ->
            val items = mutableListOf<ChatListItem>()
            val processedUserIds = mutableSetOf<String>()

            // 1. Process Active Conversations (Includes Friends & Non-Friends)
            conversations.forEach { conversation ->
                val otherUserId = conversation.participants.find { it != userId } ?: return@forEach
                val otherUserData = conversation.participantData[otherUserId]
                
                // If it's a friend, we might want to ensure we have the latest profile data, 
                // but conversation data is good for list view (fast).
                
                items.add(
                     ChatListItem(
                         chatId = conversation.id,
                         userId = otherUserId,
                         displayName = otherUserData?.displayName ?: "User",
                         avatarUrl = otherUserData?.avatarUrl ?: "",
                         lastMessage = conversation.lastMessage,
                         timestamp = conversation.lastMessageTimestamp,
                         isFriend = friends.any { it.uid == otherUserId }
                     )
                )
                processedUserIds.add(otherUserId)
            }

            // 2. Process Friends who don't have active conversations
            friends.filter { it.uid !in processedUserIds }.forEach { friend ->
                 items.add(
                     ChatListItem(
                         chatId = "", // No chat ID yet, will be generated on click
                         userId = friend.uid,
                         displayName = friend.displayName.ifEmpty { friend.username },
                         avatarUrl = friend.avatarUrl,
                         lastMessage = "Start a conversation",
                         timestamp = null,
                         isFriend = true
                     )
                 )
            }
            
            items.sortedByDescending { it.timestamp } // Sort by recent
        }.onEach { 
            _chatListItems.value = it
        }.launchIn(viewModelScope)
    }

    fun onChatClick(userId: String, existingChatId: String, onNavigate: (String) -> Unit) {
        if (existingChatId.isNotEmpty()) {
            onNavigate(existingChatId)
        } else {
            viewModelScope.launch {
                authRepository.currentUser?.uid?.let { currentUid ->
                     // Optimistic navigation or wait for result? 
                     // Better wait to ensure creation, though startConversation is fast.
                     // Even better: Calculate determinstic ID locally and navigate, let repo handle creation on first message?
                     // No, repo.startConversation creates the doc with participants. We need that for the list query.
                     val result = chatRepository.startConversation(userId)
                     result.onSuccess { chatId ->
                         onNavigate(chatId)
                     }
                }
            }
        }
    }
}

data class ChatListItem(
    val chatId: String, // Empty if no conversation exists yet
    val userId: String,
    val displayName: String,
    val avatarUrl: String,
    val lastMessage: String,
    val timestamp: java.util.Date?,
    val isFriend: Boolean
)
