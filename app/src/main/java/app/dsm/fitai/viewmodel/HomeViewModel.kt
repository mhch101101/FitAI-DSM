package app.dsm.fitai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dsm.fitai.domain.model.Routine
import app.dsm.fitai.domain.model.User
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.RoutineRepository
import app.dsm.fitai.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    fun refreshData() {
        loadUserData()
    }

    fun createRoutine() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUid() ?: return@launch
            val user = userRepository.getUser(uid) ?: return@launch
            
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val newRoutine = routineRepository.generateDefaultRoutine(uid, user.objective)
                _uiState.value = _uiState.value.copy(
                    routine = newRoutine,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUid() ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true)

            val user = userRepository.getUser(uid)
            val routine = routineRepository.getRoutine(uid)

            _uiState.value = _uiState.value.copy(
                user = user,
                routine = routine,
                isLoading = false
            )
        }
    }
}

data class HomeUiState(
    val user: User? = null,
    val routine: Routine? = null,
    val isLoading: Boolean = false
)
