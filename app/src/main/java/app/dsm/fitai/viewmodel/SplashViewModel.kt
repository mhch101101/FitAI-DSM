package app.dsm.fitai.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dsm.fitai.data.firebase.AuthService
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state = _state

    fun checkSession(context: Context) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUid()

            if (uid == null) {
                _state.value = SplashState.GoLogin
                return@launch
            }

            val user = userRepository.getUser(uid)

            if (user != null) {
                _state.value = SplashState.GoHome
            } else {
                authRepository.signOut(context)
                _state.value = SplashState.GoLogin
            }
        }
    }
}

sealed class SplashState {
    object Loading : SplashState()
    object GoLogin : SplashState()
    object GoHome : SplashState()
}