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
    override fun sendOtp(phoneNumber: String, activity: Activity, onCodeSent: (String) -> Unit, onError: (String) -> Unit){
        return authService.sendOtp(phoneNumber,activity,onCodeSent,onError)
    }
    override fun verifyOtp(verificationId: String, code: String, onResult: (Boolean) -> Unit){
        return authService.verifyOtp(verificationId,code,onResult)
    }
    override suspend fun signOut(context: Context) = authService.logout(context)
}