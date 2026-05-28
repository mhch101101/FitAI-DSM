package app.dsm.fitai.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.viewmodel.LoginViewModel
import app.dsm.fitai.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navigateToLogin: () -> Unit = {},
    navigateToHome: () -> Unit = {},
    navigateToProfileSetup: () -> Unit = {}
) {

    val appComponent = (LocalContext.current.applicationContext as FitAIApp).appComponent

    val viewModel = remember {
        RegisterViewModel(
            authRepository = appComponent.authRepository()
        )
    }

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) {
            navigateToProfileSetup()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // =======================
                // HEADER
                // =======================
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Únete a FitAI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                Text(
                    text = "Crea tu cuenta y empieza a entrenar inteligente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                // =======================
                // EMAIL
                // =======================
                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text("Correo") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                // =======================
                // PASSWORD
                // =======================
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChanged,
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (state.isPasswordVisible)
                        VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible)
                                    Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))

                // =======================
                // CONFIRM PASSWORD (CON OJITO)
                // =======================
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChanged,
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (state.isConfirmPasswordVisible)
                        VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                            Icon(
                                imageVector = if (state.isConfirmPasswordVisible)
                                    Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                // =======================
                // ERROR
                // =======================
                state.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(10.dp))
                }

                // =======================
                // BUTTON
                // =======================
                Button(
                    onClick = { viewModel.register() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Crear cuenta")
                    }
                }

                Spacer(Modifier.height(10.dp))

                TextButton(onClick = navigateToLogin) {
                    Text("Ya tengo cuenta")
                }
            }
        }
    }
}