package app.dsm.fitai.data.repository

import app.dsm.fitai.data.local.dao.ChatDao
import app.dsm.fitai.data.local.database.ChatEntity
import app.dsm.fitai.domain.model.ChatMessage
import app.dsm.fitai.domain.model.ChatRole
import app.dsm.fitai.domain.repository.ChatRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao
) : ChatRepository {

    // Reemplaza con tu API KEY real
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "TU_API_KEY_AQUI"
    )

    override fun getChatMessages(): Flow<List<ChatMessage>> {
        return chatDao.getAllMessages().map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    content = entity.content,
                    role = ChatRole.valueOf(entity.role),
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun sendMessage(content: String) {
        // 1. Guardar mensaje del usuario
        val userMessage = ChatMessage(content = content, role = ChatRole.USER)
        saveToLocal(userMessage)

        try {
            // 2. Obtener respuesta de Gemini
            // Instrucción de sistema implícita para FitAI
            val response = generativeModel.generateContent(content {
                text("Eres FitAI, un asistente experto en fitness y nutrición. " +
                        "Ayuda al usuario con sus rutinas, ejercicios y dieta. " +
                        "Responde de forma motivadora y breve.\n\nUsuario: $content")
            })
            
            val modelResponse = response.text ?: "No pude procesar tu solicitud."
            
            // 3. Guardar respuesta del modelo
            val modelMessage = ChatMessage(content = modelResponse, role = ChatRole.MODEL)
            saveToLocal(modelMessage)

        } catch (e: Exception) {
            val errorMessage = ChatMessage(
                content = "Error de conexión: ${e.localizedMessage}",
                role = ChatRole.ERROR
            )
            saveToLocal(errorMessage)
        }
    }

    override suspend fun clearChat() {
        chatDao.deleteAllMessages()
    }

    private suspend fun saveToLocal(message: ChatMessage) {
        chatDao.insertMessage(
            ChatEntity(
                id = message.id,
                content = message.content,
                role = message.role.name,
                timestamp = message.timestamp
            )
        )
    }
}
