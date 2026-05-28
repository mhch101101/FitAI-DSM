package app.dsm.fitai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.dsm.fitai.ui.screens.auth.LoginScreen
import app.dsm.fitai.ui.screens.auth.RegisterScreen
import app.dsm.fitai.ui.screens.home.HomeScreen
import app.dsm.fitai.ui.screens.onboarding.ProfileSetupScreen
import app.dsm.fitai.ui.screens.splash.SplashScreen

@Composable
fun FitAINavigation(
    navHostController: NavHostController
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
                navigateToRegister={navHostController.navigate(Screen.Register.route)}
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navigateToLogin={navHostController.navigate(Screen.Login.route)},
                navigateToHome={navHostController.navigate(Screen.Home.route)}
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen()
        }
    }
}