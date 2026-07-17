package app.dsm.fitai.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.StepRepository
import app.dsm.fitai.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import app.dsm.fitai.data.sensor.StepSensorManager
import javax.inject.Inject

class LayoutViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val stepRepository: StepRepository,
    private val stepSensorManager: StepSensorManager
) : ViewModel() {

    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent = _logoutEvent.asStateFlow()

    private val _userName = MutableStateFlow("Usuario")
    val userName = _userName.asStateFlow()

    val todaySteps = stepRepository.getTodaySteps()

    init {
        loadUser()
    }

    // Called from the UI once the ACTIVITY_RECOGNITION permission is confirmed granted.
    fun startStepTracking() {
        stepSensorManager.startListening()
    }

    override fun onCleared() {
        super.onCleared()
        stepSensorManager.stopListening()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val uid=authRepository.getCurrentUid()
            val user = userRepository.getUser(uid.orEmpty())
            _userName.value = user?.name ?: "Usuario"
        }
    }
    fun logout(context: Context) {
        viewModelScope.launch {
            authRepository.signOut(context)
            _logoutEvent.value = true
        }
    }
}
