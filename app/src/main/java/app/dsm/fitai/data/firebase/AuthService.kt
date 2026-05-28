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
    //private val googleSignInClient: GoogleSignInClient
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

    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    firebaseAuth.signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onError(e.message ?: "Error")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    onCodeSent(verificationId)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(
        verificationId: String,
        code: String,
        onResult: (Boolean) -> Unit
    ) {

        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }
    }

}