package app.dsm.fitai.domain.repository

import app.dsm.fitai.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatMessages(): Flow<List<ChatMessage>>
    suspend fun sendMessage(content: String)
    suspend fun clearChat()
}
