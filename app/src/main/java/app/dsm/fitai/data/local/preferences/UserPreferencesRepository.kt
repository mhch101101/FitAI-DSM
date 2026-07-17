package app.dsm.fitai.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import app.dsm.fitai.ui.theme.ContrastLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val context: Context
) {
    private object PreferencesKeys {
        val CONTRAST_LEVEL = stringPreferencesKey("contrast_level")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val contrastLevelStr = preferences[PreferencesKeys.CONTRAST_LEVEL] ?: ContrastLevel.NORMAL.name
            val contrastLevel = try {
                ContrastLevel.valueOf(contrastLevelStr)
            } catch (e: Exception) {
                ContrastLevel.NORMAL
            }

            val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeModeStr)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }

            UserPreferences(contrastLevel, themeMode)
        }

    suspend fun updateContrastLevel(contrastLevel: ContrastLevel) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CONTRAST_LEVEL] = contrastLevel.name
        }
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }
}

data class UserPreferences(
    val contrastLevel: ContrastLevel,
    val themeMode: ThemeMode
)
