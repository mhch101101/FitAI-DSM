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
import app.dsm.fitai.ui.screens.profile.ProfileEditScreen
import app.dsm.fitai.ui.screens.progress.ProgressScreen
import app.dsm.fitai.ui.screens.splash.SplashScreen

@Composable
fun FitAINavigation(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navHostController,
        startDestination = Screen.Splash.route
    ) {

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
                navigateToProgress={navHostController.navigate(Screen.Progress.route)}
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
    }
}