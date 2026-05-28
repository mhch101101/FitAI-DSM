package app.dsm.fitai.data.firebase

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await
import app.dsm.fitai.R
import com.google.firebase.FirebaseException
import java.util.concurrent.TimeUnit

class AuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun hasSession(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    private fun getGoogleSignInClient(context: Context): GoogleSignInClient {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun logout(context: Context) {
        firebaseAuth.signOut()
        val googleSignInClient =getGoogleSignInClient(context)
        googleSignInClient.signOut()
            .addOnCompleteListener {
                googleSignInClient.revokeAccess()
            }
    }

    fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    suspend fun loginWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<String> {
        return try {

            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val uid = result.user?.uid ?: throw Exception("UID no encontrado")

            Result.success(uid)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}