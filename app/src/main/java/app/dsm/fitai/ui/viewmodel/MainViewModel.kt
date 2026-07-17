package app.dsm.fitai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dsm.fitai.data.local.preferences.ThemeMode
import app.dsm.fitai.data.local.preferences.UserPreferences
import app.dsm.fitai.data.local.preferences.UserPreferencesRepository
import app.dsm.fitai.ui.theme.ContrastLevel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = repository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(ContrastLevel.NORMAL, ThemeMode.SYSTEM)
        )

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            repository.updateThemeMode(mode)
        }
    }

    fun updateContrastLevel(level: ContrastLevel) {
        viewModelScope.launch {
            repository.updateContrastLevel(level)
        }
    }
}
