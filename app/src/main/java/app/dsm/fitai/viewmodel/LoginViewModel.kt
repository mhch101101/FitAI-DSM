package app.dsm.fitai.viewmodel

import androidx.lifecycle.ViewModel
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
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

        _uiState.value = state.copy(
            isLoading = true,
            errorMessage = null
        )

        CoroutineScope(Dispatchers.IO).launch {
            /*val result = authRepository.loginWithEmail(
                email = state.email,
                password = state.password
            )

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.message,
                isLoggedIn = result.isSuccess
            )*/
        }
    }

    fun onGoogleTokenReceived(idToken: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        authRepository.loginWithGoogle(idToken) { success ->
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = if (!success) "Error login Google" else null
            )
        }
    }

}

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoggedIn: Boolean = false
)