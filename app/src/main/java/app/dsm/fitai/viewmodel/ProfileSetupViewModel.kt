package app.dsm.fitai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dsm.fitai.domain.model.User
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ProfileSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState = _uiState.asStateFlow()
    init {
        _uiState.value = _uiState.value.copy(
            gender = "Masculino"
        )
    }
    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value)
    }

    fun onGenderChange(value: String) {
        _uiState.value = _uiState.value.copy(gender = value)
    }

    fun onBirthDateSelected(value: Long) {
        _uiState.value = _uiState.value.copy(
            birthDate = value,
            birthDateText = formatDate(value)
        )
    }

    private fun formatDate(time: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(time))
    }

    fun saveProfile() {
        val state = _uiState.value
        if (state.firstName.isBlank() ||
            state.lastName.isBlank() ||
            state.birthDate == 0L ||
            state.gender.isBlank()
        ) {
            _uiState.value = state.copy(error = "Todos los campos son obligatorios")
            return
        }
        _uiState.value = state.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val uuid = authRepository.getCurrentUid().orEmpty()
            try {
                userRepository.saveUserProfileInit(
                    User(
                        uid = uuid,
                        name = state.firstName,
                        lastName = state.lastName,
                        birthDate = state.birthDate,
                        sex = state.gender,
                    )
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCompleted = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al guardar perfil"
                )
            }
        }
    }
}

data class ProfileSetupUiState(
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: Long = 0L,
    val birthDateText: String = "",
    val gender: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCompleted: Boolean = false
)