package app.dsm.fitai.ui.navigation

sealed class Screen(val route: String) {

    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Register : Screen("register")
    object ProfileSetup : Screen("profile_setup")

}