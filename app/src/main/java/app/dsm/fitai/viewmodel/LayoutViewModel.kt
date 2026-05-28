package app.dsm.fitai.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LayoutViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) :ViewModel() {

    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent = _logoutEvent.asStateFlow()

    fun logout(context: Context) {
        viewModelScope.launch {
            authRepository.signOut(context)
            _logoutEvent.value = true
        }
    }
}