package app.dsm.fitai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import app.dsm.fitai.ui.navigation.FitAINavigation
import app.dsm.fitai.ui.screens.auth.LoginScreen
import app.dsm.fitai.ui.screens.splash.SplashScreen
import app.dsm.fitai.ui.theme.FitAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            FitAITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FitAINavigation(navController)
                    /*SplashScreen(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )*/
                    //LoginScreen()
                    /*Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )*/
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