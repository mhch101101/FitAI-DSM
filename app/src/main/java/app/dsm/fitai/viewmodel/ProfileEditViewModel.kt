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


class ProfileEditViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser(){
        viewModelScope.launch {
            try {
                val uid = authRepository.getCurrentUid().orEmpty()

                if (uid.isBlank()) return@launch

                val user = userRepository.getUser(uid)

                _uiState.value = _uiState.value.copy(
                    uid = uid,
                    firstName = user?.name.orEmpty(),
                    lastName = user?.lastName.orEmpty(),
                    gender = user?.sex.orEmpty(),
                    birthDate = user?.birthDate ?: 0L,
                    birthDateText =
                        if (user?.birthDate != 0L)
                            formatDate(user?.birthDate ?: 0L)
                        else ""
                )
            }catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    error = "No se pudo cargar el perfil"
                )
            }
        }
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

    fun clearCompletedState() {
        _uiState.value = _uiState.value.copy(
            isCompleted = false
        )
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
            try {
                userRepository.saveUserProfile(
                    User(
                        uid = state.uid,
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

data class ProfileEditUiState(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: Long = 0L,
    val birthDateText: String = "",
    val gender: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCompleted: Boolean = false
)
