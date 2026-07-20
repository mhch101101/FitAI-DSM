package app.dsm.fitai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.dsm.fitai.ui.screens.auth.LoginScreen
import app.dsm.fitai.ui.screens.auth.RegisterScreen
import app.dsm.fitai.ui.screens.home.HomeScreen
import app.dsm.fitai.ui.screens.onboarding.ProfileSetupScreen
import app.dsm.fitai.ui.screens.chat.ChatScreen
import app.dsm.fitai.ui.screens.profile.ProfileEditScreen
import app.dsm.fitai.ui.screens.progress.ProgressScreen
import app.dsm.fitai.ui.screens.splash.SplashScreen
import app.dsm.fitai.viewmodel.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import app.dsm.fitai.di.FitAIApp
import androidx.compose.ui.platform.LocalContext

@Composable
fun FitAINavigation(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appComponent = (context.applicationContext as FitAIApp).appComponent

    NavHost(
        navController = navHostController,
        startDestination = Screen.Splash.route
    ) {
        // ... (otros composables previos)
        composable(Screen.Splash.route) {
            SplashScreen(
                navigateToLogin={navHostController.navigate(Screen.Login.route)},
                navigateToHome={navHostController.navigate(Screen.Home.route)}
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                navigateToHome={navHostController.navigate(Screen.Home.route)},
                navigateToRegister={navHostController.navigate(Screen.Register.route)},
                navigateToProfileSetup={navHostController.navigate(Screen.ProfileSetup.route)}
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navigateToLogin={navHostController.navigate(Screen.Login.route)},
                navigateToHome={navHostController.navigate(Screen.Home.route)},
                navigateToProfileSetup={navHostController.navigate(Screen.ProfileSetup.route)}
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navigateToLogin={navHostController.navigate(Screen.Login.route)},
                navigateToProfileEdit={navHostController.navigate(Screen.ProfileEdit.route)},
                navigateToProgress={navHostController.navigate(Screen.Progress.route)},
                navigateToChat={navHostController.navigate(Screen.Chat.route)}
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                navigateToLogin={navHostController.navigate(Screen.Login.route)},
                navigateToProfileEdit={navHostController.navigate(Screen.ProfileEdit.route)},
                onNavigateBack={navHostController.popBackStack()}
            )
        }

        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(
                navigateToHome={navHostController.navigate(Screen.Home.route)}
            )
        }

        composable(Screen.ProfileEdit.route) {
            ProfileEditScreen(
                navigateToLogin={navHostController.navigate(Screen.Login.route)},
                navigateToHome={navHostController.navigate(Screen.Home.route)}
            )
        }

        composable(Screen.Chat.route) {
            val chatViewModel: ChatViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return ChatViewModel(appComponent.chatRepository()) as T
                    }
                }
            )
            ChatScreen(
                viewModel = chatViewModel,
                onNavigateBack = { navHostController.popBackStack() }
            )
        }
    }
}
