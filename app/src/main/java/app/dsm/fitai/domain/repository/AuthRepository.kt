package app.dsm.fitai.domain.repository

import android.app.Activity
import android.content.Context

interface AuthRepository {
    suspend fun hasSession(): Boolean
    suspend fun getCurrentUid(): String?
    fun loginWithGoogle(idToken: String, onResult: (Boolean) -> Unit)
    fun sendOtp(phoneNumber: String, activity: Activity, onCodeSent: (String) -> Unit, onError: (String) -> Unit)
    fun verifyOtp(verificationId: String, code: String, onResult: (Boolean) -> Unit)
    suspend fun signOut(context: Context)
}