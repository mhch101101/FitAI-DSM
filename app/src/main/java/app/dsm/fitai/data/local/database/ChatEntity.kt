package app.dsm.fitai.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey val id: String,
    val content: String,
    val role: String, // "USER", "MODEL", "ERROR"
    val timestamp: Long
)
