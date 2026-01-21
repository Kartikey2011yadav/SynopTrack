package com.example.synoptrack.social.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synoptrack.auth.domain.repository.AuthRepository
import com.example.synoptrack.profile.domain.model.UserProfile
import com.example.synoptrack.social.domain.model.Group
import com.example.synoptrack.social.domain.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val authRepository: AuthRepository,
    private val chatRepository: com.example.synoptrack.social.domain.repository.ChatRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(ChatFilter.ALL)
    val selectedFilter: StateFlow<ChatFilter> = _selectedFilter.asStateFlow()

    // Replaces _friends for the Chat List
    private val _chatItems = MutableStateFlow<List<ChatItemState>>(emptyList())
    val chatItems: StateFlow<List<ChatItemState>> = _chatItems.asStateFlow()
    
    // Keep _groups for now if needed, or merge into chats if design implies unified list.
    // Design has "Contacts" (Friends), "Unknown", "New". Groups probably "All".
    // For now, I'll keep groups separate or accessible via tabs? 
    // Screenshot shows "Messages" with filters. I'll focus on the merged list.

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val uid = authRepository.currentUser?.uid
        if (uid != null) {
            observeData(uid)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: ChatFilter) {
        _selectedFilter.value = filter
    }

    private fun observeData(uid: String) {
        viewModelScope.launch {
            launch { socialRepository.getUserGroups(uid).collect { _groups.value = it } }

            val friendsFlow = socialRepository.getFriends(uid)
            val conversationsFlow = chatRepository.getConversations(uid)
            
            // Combine all inputs: Friends, Conversations, Search, Filter
            combine(friendsFlow, conversationsFlow, _searchQuery, _selectedFilter) { friends, conversations, query, filter ->
                 val items = mutableListOf<ChatItemState>()
                 val friendIds = friends.map { it.uid }.toSet()
                 
                 // 1. Process Active Conversations (Source of Truth for Chats)
                 conversations.forEach { conversation ->
                     val otherUid = conversation.participants.find { it != uid } ?: return@forEach
                     val participantData = conversation.participantData[otherUid]
                     val isFriend = friendIds.contains(otherUid)
                     val unread = conversation.unreadCounts[uid] ?: 0
                     
                     // Filter Logic: UNKNOWN
                     if (filter == ChatFilter.UNKNOWN && isFriend) return@forEach
                     
                     // Filter Logic: NEW
                     if (filter == ChatFilter.NEW && unread == 0) return@forEach

                     items.add(
                         ChatItemState(
                             id = conversation.id, // Chat ID
                             targetUserId = otherUid,
                             name = participantData?.displayName ?: "User",
                             avatarUrl = participantData?.avatarUrl ?: "",
                             lastMessage = conversation.lastMessage,
                             timestamp = conversation.lastMessageTimestamp,
                             unreadCount = unread,
                             isFriend = isFriend,
                             isOnline = false // TODO: Real Presence
                         )
                     )
                 }

                 // 2. Process Friends without Active Conversations (Only if filtering allows)
                 // If Filter is NEW, we skip empty chats.
                 // If Filter is UNKNOWN, we skip friends.
                 if (filter != ChatFilter.NEW && filter != ChatFilter.UNKNOWN) {
                     val existingChatUserIds = items.map { it.targetUserId }.toSet()
                     friends.filter { !existingChatUserIds.contains(it.uid) }.forEach { friend ->
                         items.add(
                             ChatItemState(
                                 id = "", // No Chat ID yet
                                 targetUserId = friend.uid,
                                 name = friend.displayName.ifEmpty { friend.username },
                                 avatarUrl = friend.avatarUrl,
                                 lastMessage = "Start a conversation",
                                 timestamp = null,
                                 unreadCount = 0,
                                 isFriend = true,
                                 isOnline = false
                             )
                         )
                     }
                 }

                 // 3. Apply Search
                 val filteredItems = if (query.isEmpty()) {
                     items
                 } else {
                     items.filter { it.name.contains(query, ignoreCase = true) }
                 }
                 
                 // 4. Sort (Unread > Recent > Name)
                 filteredItems.sortedWith(
                     compareByDescending<ChatItemState> { it.timestamp }.thenByDescending { it.unreadCount }
                 )
            }.collect {
                _chatItems.value = it
            }
        }
    }

    fun onChatClick(target: String, onNavigate: (String) -> Unit) {
        viewModelScope.launch {
             if (target.contains("_") || target.length > 20) { 
                 onNavigate(target)
             } else {
                 startNewChat(target, onNavigate)
             }
        }
    }
    
    fun startNewChat(targetUserId: String, onNavigate: (String) -> Unit) {
        viewModelScope.launch {
            chatRepository.startConversation(targetUserId).onSuccess { chatId ->
                onNavigate(chatId)
            }
        }
    }

    fun addFriend(inviteCode: String) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = socialRepository.addFriend(inviteCode, uid)
            if (result.isSuccess) {
                _toastMessage.value = "Friend added successfully!"
            } else {
                _toastMessage.value = "Failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun createGroup(name: String) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            socialRepository.createGroup(name, uid)
        }
    }

    fun joinGroup(code: String) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            socialRepository.joinGroup(code, uid)
        }
    }
    
    fun clearToast() {
        _toastMessage.value = null
    }
}

enum class ChatFilter {
    ALL,
    CONTACTS, // Friends
    UNKNOWN,  // Non-Friends
    NEW       // Unread
}

data class ChatItemState(
    val id: String,
    val targetUserId: String,
    val name: String,
    val avatarUrl: String,
    val lastMessage: String,
    val timestamp: java.util.Date?,
    val unreadCount: Int,
    val isFriend: Boolean,
    val isOnline: Boolean
)
