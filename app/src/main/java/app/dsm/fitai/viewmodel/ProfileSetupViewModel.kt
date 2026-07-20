package app.dsm.fitai.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dsm.fitai.domain.model.User
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.StepRepository
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
    private val userRepository: UserRepository,
    private val stepRepository: StepRepository
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

    fun onWeightChange(value: String) {
        _uiState.value = _uiState.value.copy(weight = value)
    }

    fun onObjectiveChange(value: String) {
        _uiState.value = _uiState.value.copy(selectedObjective = value)
    }

    fun onLevelChange(value: String) {
        _uiState.value = _uiState.value.copy(selectedLevel = value)
    }

    fun onFrequencyChange(value: Int) {
        _uiState.value = _uiState.value.copy(trainingFrequency = value)
    }

    fun onDurationChange(value: Int) {
        _uiState.value = _uiState.value.copy(trainingDuration = value)
    }

    fun onBirthDateSelected(value: Long) {
        _uiState.value = _uiState.value.copy(
            birthDate = value,
            birthDateText = formatDate(value)
        )
    }

    fun nextStep() {
        val current = _uiState.value.currentStep
        if (current < 3) {
            _uiState.value = _uiState.value.copy(currentStep = current + 1)
        }
    }

    fun previousStep() {
        val current = _uiState.value.currentStep
        if (current > 1) {
            _uiState.value = _uiState.value.copy(currentStep = current - 1)
        }
    }

    private fun formatDate(time: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(time))
    }

    fun saveProfile() {
        val state = _uiState.value
        val weightValue = state.weight.toFloatOrNull() ?: 0f
        if (state.firstName.isBlank() ||
            state.lastName.isBlank() ||
            state.birthDate == 0L ||
            state.gender.isBlank() ||
            weightValue <= 0f ||
            state.selectedObjective.isBlank() ||
            state.selectedLevel.isBlank() ||
            state.trainingFrequency == 0 ||
            state.trainingDuration == 0
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
                        weight = weightValue,
                        objective = state.selectedObjective,
                        level = state.selectedLevel,
                        trainingFrequency = state.trainingFrequency,
                        trainingDuration = state.trainingDuration
                    )
                )

                // Set step goal based on objective (AI Logic)
                val goal = stepRepository.getRecommendedGoal(state.selectedObjective)
                stepRepository.setStepGoal(goal)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCompleted = true
                )

            } catch (e: Exception) {
                Log.e("ProfileSetupViewModel", "Error al guardar perfil", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al guardar perfil"
                )
            }
        }
    }
}

data class ProfileSetupUiState(
    val currentStep: Int = 1,
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: Long = 0L,
    val birthDateText: String = "",
    val gender: String = "",
    val weight: String = "",
    val selectedObjective: String = "",
    val selectedLevel: String = "",
    val trainingFrequency: Int = 0,
    val trainingDuration: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCompleted: Boolean = false
)