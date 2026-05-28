package app.dsm.fitai.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navigateToLogin: () -> Unit = {},
    onCreateRoutine: () -> Unit = {}
) {
    val context = LocalContext.current
    val appComponent = (context.applicationContext as FitAIApp).appComponent

    val viewModel = remember {
        HomeViewModel(
            userRepository = appComponent.userRepository(),
            authRepository = appComponent.authRepository()
        )
    }

    LayoutScreen(
        context =context,
        navigateToLogin=navigateToLogin
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        "Hoy es un gran día",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(6.dp))

                    Text("Crea tu rutina con IA y mejora tu rendimiento")
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onCreateRoutine,
                modifier = Modifier.size(170.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Crear Rutina")
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "IA personalizada en segundos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}