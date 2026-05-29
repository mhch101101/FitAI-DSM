package app.dsm.fitai.ui.screens.home

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.viewmodel.LayoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutScreen(
    title: String = "FitAI",
    context: Context,
    showStepsCard: Boolean = true,
    navigateToLogin: () -> Unit = {},
    navigateToProfileEdit: () -> Unit = {},
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
    val userName by viewModel.userName.collectAsState()
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
                            text = "Bienvenido, ${if (userName.isBlank()) "Usuario" else userName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                },
                actions = {

                    IconButton(onClick = {navigateToProfileEdit()}) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Editar perfil",
                            modifier = Modifier.size(32.dp)
                        )
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

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                content()
            }

            if (showStepsCard) {

                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1B5E20).copy(alpha = 0.12f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    "🔥",
                                    fontSize = 22.sp
                                )

                                Spacer(Modifier.width(10.dp))

                                Column {

                                    Text(
                                        "Pasos de hoy",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        "$steps",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color(0xFF66BB6A)
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {

                                Text(
                                    "Meta",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    "$stepsGoal",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = {
                                (steps.toFloat() / stepsGoal.toFloat())
                                    .coerceIn(0f, 1f)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),

                            color = Color(0xFF4CAF50),

                            trackColor = Color(0xFF4CAF50)
                                .copy(alpha = 0.18f)
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "${((steps.toFloat() / stepsGoal.toFloat()) * 100).toInt()}% completado",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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