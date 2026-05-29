package app.dsm.fitai.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) :ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onTogglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun loginWithEmail() {
        val state = _uiState.value

        if (state.email.isBlank()) {
            _uiState.value = state.copy(
                errorMessage = "Ingresa tu correo",
                isLoading = false
            )
            return
        }

        if (state.password.isBlank()) {
            _uiState.value = state.copy(
                errorMessage = "Ingresa tu contraseña",
                isLoading = false
            )
            return
        }

        _uiState.value = state.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            val result = authRepository.loginWithEmail(
                email = state.email,
                password = state.password
            )
            var isIncompleteProfile = false

            if(result.isSuccess){
                val uuid=result.getOrNull().orEmpty()
                var user =userRepository.getUser(uuid)
                isIncompleteProfile = user==null || user.isProfileIncomplete()
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.let { error ->
                    mapAuthError(error)
                },
                isLoggedIn = result.isSuccess,
                isIncompleteProfile = isIncompleteProfile
            )
        }
    }

    fun onGoogleTokenReceived(idToken: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        authRepository.loginWithGoogle(idToken) { uuid ->
            if (uuid.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    errorMessage = "Error login Google"
                )
                return@loginWithGoogle
            }
            viewModelScope.launch {

                val user = userRepository.getUser(uuid)
                Log.d("--user--",user?.uid.orEmpty())
                val isIncompleteProfile = user == null || user.isProfileIncomplete()
                Log.d("--user--",isIncompleteProfile.toString())

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    isIncompleteProfile = isIncompleteProfile,
                    errorMessage = null
                )
            }
        }
    }

}

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isIncompleteProfile: Boolean = false
)

public fun mapAuthError(error: Throwable): String {
    val code = (error as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
    Log.d("code--",code.toString())
    return when (code) {

        "ERROR_USER_NOT_FOUND" ->
            "El correo no está registrado"

        "ERROR_WRONG_PASSWORD" ->
            "Contraseña incorrecta"

        "ERROR_INVALID_EMAIL" ->
            "Correo inválido"

        "ERROR_NETWORK_REQUEST_FAILED" ->
            "Error de conexión"

        "ERROR_EMAIL_ALREADY_IN_USE" ->
            "Este correo ya está registrado"

        "ERROR_INVALID_CREDENTIAL" ->
            "Credenciales inválidas"

        else ->
            "Error al iniciar sesión"
    }
}