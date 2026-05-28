package app.dsm.fitai.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.UserRepository
import app.dsm.fitai.viewmodel.SplashViewModel
import javax.inject.Inject

class SplashViewModelFactory @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return SplashViewModel(
            userRepository,
            authRepository
        ) as T
    }
}