package app.dsm.fitai.viewmodel

import androidx.lifecycle.ViewModel
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.UserRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) :ViewModel() {


}