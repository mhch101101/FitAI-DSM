package app.dsm.fitai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dsm.fitai.domain.model.ChatMessage
import app.dsm.fitai.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val messages: StateFlow<List<ChatMessage>> = chatRepository.getChatMessages()
        .asStateFlow(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            chatRepository.sendMessage(content)
            _isLoading.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            chatRepository.clearChat()
        }
    }

    private fun <T> kotlinx.coroutines.flow.Flow<T>.asStateFlow(initialValue: T): StateFlow<T> {
        val flow = MutableStateFlow(initialValue)
        viewModelScope.launch {
            collect { flow.value = it }
        }
        return flow.asStateFlow()
    }
}
