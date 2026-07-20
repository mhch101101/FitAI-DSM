package app.dsm.fitai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import app.dsm.fitai.data.local.preferences.ThemeMode
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.ui.navigation.FitAINavigation
import app.dsm.fitai.ui.screens.auth.LoginScreen
import app.dsm.fitai.ui.screens.splash.SplashScreen
import app.dsm.fitai.ui.theme.ContrastLevel
import app.dsm.fitai.ui.theme.FitAITheme
import app.dsm.fitai.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appComponent = (application as FitAIApp).appComponent
        val userPreferencesRepository = appComponent.userPreferencesRepository()

        setContent {
            val context = LocalContext.current
            val mainViewModel: MainViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return MainViewModel(userPreferencesRepository) as T
                    }
                }
            )

            // Launcher para solicitar el permiso de notificaciones
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                // Aquí podrías manejar si el usuario deniega el permiso
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            val userPreferences by mainViewModel.userPreferences.collectAsState()

            val darkTheme = when (userPreferences.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val navController = rememberNavController()

            FitAITheme(
                darkTheme = darkTheme,
                contrastLevel = userPreferences.contrastLevel
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FitAINavigation(
                        navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FitAITheme {
        Greeting("Android")
    }
}