package app.dsm.fitai.domain.model

import java.util.UUID

enum class ChatRole {
    USER, MODEL, ERROR
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val role: ChatRole,
    val timestamp: Long = System.currentTimeMillis()
)
