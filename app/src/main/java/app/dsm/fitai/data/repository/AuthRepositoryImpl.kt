package app.dsm.fitai.data.repository

import android.app.Activity
import android.content.Context
import app.dsm.fitai.data.firebase.AuthService
import app.dsm.fitai.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {

    override suspend fun hasSession() = authService.hasSession()

    override suspend fun getCurrentUid() = authService.getCurrentUserId()

    override fun loginWithGoogle(idToken: String, onResult: (Boolean) -> Unit) {
        authService.firebaseAuthWithGoogle(idToken, onResult)
    }
    override suspend fun signOut(context: Context) = authService.logout(context)

    override suspend fun loginWithEmail(email: String, password: String): Result<String> {
        return authService.loginWithEmail(email, password)
    }

    override suspend fun registerWithEmail(email: String, password: String): Result<String>{
        return authService.registerWithEmail(email, password)
    }
}