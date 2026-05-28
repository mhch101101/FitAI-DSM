package app.dsm.fitai.domain.repository

import android.app.Activity
import android.content.Context

interface AuthRepository {
    suspend fun hasSession(): Boolean
    suspend fun getCurrentUid(): String?
    fun loginWithGoogle(idToken: String, onResult: (Boolean) -> Unit)
    suspend fun signOut(context: Context)
    suspend fun loginWithEmail(email: String, password: String): Result<String>
    suspend fun registerWithEmail(email: String, password: String): Result<String>
}