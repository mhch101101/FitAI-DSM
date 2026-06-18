package app.dsm.fitai.ui.screens.splash

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import app.dsm.fitai.R
import app.dsm.fitai.data.firebase.AuthService
import app.dsm.fitai.data.firebase.UserFirestore
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.UserRepository
import app.dsm.fitai.ui.navigation.Screen
import app.dsm.fitai.viewmodel.SplashState
import app.dsm.fitai.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit = {},
    navigateToHome: () -> Unit = {}
) {

    val context = LocalContext.current
    val appComponent =(LocalContext.current.applicationContext as FitAIApp).appComponent
    val viewModel = remember {
        SplashViewModel(
            userRepository = appComponent.userRepository(),
            authRepository = appComponent.authRepository()
        )
    }
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.checkSession(context)
    }
    LaunchedEffect(state) {
        when (state) {
            SplashState.GoLogin -> { navigateToLogin() }
            SplashState.GoHome -> { navigateToHome() }
            else -> Unit
        }
    }
    SplashUI()
}

@Composable
fun SplashUI() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            /*Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )*/

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            CircularProgressIndicator()
        }
    }
}