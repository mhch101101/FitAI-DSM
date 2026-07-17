package app.dsm.fitai.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dsm.fitai.R
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
            authRepository = appComponent.authRepository(),
            stepRepository = appComponent.stepRepository(),
            stepSensorManager = appComponent.provideStepSensorManager()
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results[Manifest.permission.ACTIVITY_RECOGNITION] != false) {
            viewModel.startStepTracking()
        }
    }

    LaunchedEffect(Unit) {
        val missingPermissions = buildList {
            // ACTIVITY_RECOGNITION is a runtime permission only from API 29 onwards.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                add(Manifest.permission.ACTIVITY_RECOGNITION)
            }

            // POST_NOTIFICATIONS is a runtime permission only from API 33 onwards,
            // needed to show the "goal reached" notification from StepSyncWorker.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        } else {
            viewModel.startStepTracking()
        }
    }

    val logoutEvent by viewModel.logoutEvent.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val todaySteps by viewModel.todaySteps.collectAsState(initial = null)
    LaunchedEffect(logoutEvent) {
        if (logoutEvent) navigateToLogin()
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    val steps = todaySteps?.steps ?: 0
    val stepsGoal = todaySteps?.goal ?: 8000

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                title = {
                    Column {
                        val displayUserName = if (userName.isBlank()) stringResource(R.string.layout_default_user) else userName
                        Text(
                            text = stringResource(R.string.layout_welcome_user, displayUserName),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {

                    IconButton(onClick = {navigateToProfileEdit()}) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.layout_edit_profile_desc),
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = stringResource(R.string.layout_logout_desc),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                                        stringResource(R.string.layout_steps_today),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                    Text(
                                        "$steps",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {

                                Text(
                                    stringResource(R.string.layout_steps_goal_label),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Text(
                                    "$stepsGoal",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
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

                            color = MaterialTheme.colorScheme.primary,

                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            stringResource(
                                R.string.layout_steps_completion,
                                ((steps.toFloat() / stepsGoal.toFloat()) * 100).toInt()
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                icon = {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = {
                    Text(
                        stringResource(R.string.layout_logout_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.layout_logout_message),
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
                        Text(stringResource(R.string.layout_logout_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text(stringResource(R.string.layout_logout_cancel))
                    }
                },
                shape = MaterialTheme.shapes.extraLarge
            )
        }
    }
}