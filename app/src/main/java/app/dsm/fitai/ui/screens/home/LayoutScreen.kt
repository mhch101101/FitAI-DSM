package app.dsm.fitai.ui.screens.home

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.viewmodel.LayoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutScreen(
    title: String = "FitAI",
    context: Context,
    showStepsCard: Boolean = true,
    navigateToLogin: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {

    val ctx = LocalContext.current
    val appComponent = (ctx.applicationContext as FitAIApp).appComponent

    val viewModel = remember {
        LayoutViewModel(
            userRepository = appComponent.userRepository(),
            authRepository = appComponent.authRepository()
        )
    }

    val logoutEvent by viewModel.logoutEvent.collectAsState()
    LaunchedEffect(logoutEvent) {
        if (logoutEvent) navigateToLogin()
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    val notificationsCount = 1
    val steps = 8542
    val stepsGoal = 10_000

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    Color(0xFF1B5E20).copy(alpha = 0.15f),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Actívate hoy",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                actions = {

                    BadgedBox(
                        badge = {
                            if (notificationsCount > 0) {
                                Badge(
                                    containerColor = Color(0xFF4CAF50)
                                ) {
                                    Text(notificationsCount.toString())
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notificaciones"
                            )
                        }
                    }

                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Salir",
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // 🔥 CONTENIDO CENTRAL
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                content()
            }

            // 👣 CARD PASOS MÁS FITNESS / ENERGÉTICA
            if (showStepsCard) {

                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1B5E20).copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(modifier = Modifier.padding(20.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column {
                                Text(
                                    "🔥 Pasos de hoy",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    "$steps",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color(0xFF66BB6A)
                                )
                            }

                            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {

                                Text(
                                    "🎯 Meta",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    "$stepsGoal",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        LinearProgressIndicator(
                            progress = { steps.toFloat() / stepsGoal.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                icon = {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = {
                    Text(
                        "Cerrar sesión",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        "¿Quieres salir de FitAI? Tu progreso seguirá guardado.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            viewModel.logout(context)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Salir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancelar")
                    }
                },
                shape = MaterialTheme.shapes.extraLarge
            )
        }
    }
}