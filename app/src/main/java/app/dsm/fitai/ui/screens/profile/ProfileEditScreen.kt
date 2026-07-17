package app.dsm.fitai.ui.screens.profile

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import app.dsm.fitai.R
import app.dsm.fitai.data.local.preferences.ThemeMode
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.ui.screens.home.LayoutScreen
import app.dsm.fitai.ui.theme.ContrastLevel
import app.dsm.fitai.ui.viewmodel.MainViewModel
import app.dsm.fitai.viewmodel.ProfileEditViewModel
import java.util.Calendar

@Composable
fun ProfileEditScreen(
    navigateToLogin: () -> Unit = {},
    navigateToHome: () -> Unit = {}
) {

    val context = LocalContext.current
    val appComponent =
        (context.applicationContext as FitAIApp).appComponent
    val userPreferencesRepository = appComponent.userPreferencesRepository()

    val mainViewModel: MainViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(userPreferencesRepository) as T
            }
        }
    )

    val userPreferences by mainViewModel.userPreferences.collectAsState()

    val viewModel = remember {
        ProfileEditViewModel(
            authRepository = appComponent.authRepository(),
            userRepository = appComponent.userRepository()
        )
    }

    val state by viewModel.uiState.collectAsState()
    var showSuccessDialog by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(state.isCompleted) {

        if (state.isCompleted) {
            showSuccessDialog = true
            viewModel.clearCompletedState()
        }
    }

    LayoutScreen(
        context = context,
        navigateToLogin = navigateToLogin,
        showStepsCard = false
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = stringResource(R.string.profile_edit_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.profile_edit_subtitle),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = state.firstName,
                onValueChange = viewModel::onFirstNameChange,
                label = { Text(stringResource(R.string.profile_first_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = state.lastName,
                onValueChange = viewModel::onLastNameChange,
                label = { Text(stringResource(R.string.profile_last_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            Text(
                stringResource(R.string.profile_gender_label),
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                val isMale = state.gender == "Masculino"
                val isFemale = state.gender == "Femenino"

                Button(
                    onClick = {
                        viewModel.onGenderChange("Masculino")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (isMale) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,

                        contentColor =
                            if (isMale) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(stringResource(R.string.profile_gender_male))
                }

                Button(
                    onClick = {
                        viewModel.onGenderChange("Femenino")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (isFemale) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,

                        contentColor =
                            if (isFemale) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(stringResource(R.string.profile_gender_female))
                }
            }

            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = state.birthDateText,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.profile_birth_date_label))
                },
                trailingIcon = {

                    IconButton(
                        onClick = {

                            val calendar = Calendar.getInstance()

                            if (state.birthDate != 0L) {
                                calendar.timeInMillis = state.birthDate
                            }

                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val cal =Calendar.getInstance()
                                    cal.set(year, month, day)

                                    viewModel.onBirthDateSelected(
                                        cal.timeInMillis
                                    )
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    ) {

                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            state.error?.let {

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(24.dp))
            // --- LOCAL PREFERENCES SECTION ---
            Text(
                "Preferencias de Interfaz",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Theme Mode
            Text("Modo de Tema", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = userPreferences.themeMode == mode,
                        onClick = { mainViewModel.updateThemeMode(mode) },
                        label = { Text(mode.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Contrast Level
            Text("Nivel de Contraste", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContrastLevel.entries.forEach { level ->
                    FilterChip(
                        selected = userPreferences.contrastLevel == level,
                        onClick = { mainViewModel.updateContrastLevel(level) },
                        label = { Text(level.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                FilledTonalButton(
                    onClick = navigateToHome,

                    modifier = Modifier
                        .weight(1.15f)
                        .height(58.dp),

                    shape = RoundedCornerShape(18.dp),

                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {

                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.profile_edit_cancel),
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        viewModel.saveProfile()
                    },

                    modifier = Modifier
                        .weight(1.2f)
                        .height(58.dp),

                    enabled = !state.isLoading,

                    shape = RoundedCornerShape(18.dp),

                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 1.dp
                    ),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                    )
                ) {

                    if (state.isLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.3.dp
                        )

                    } else {

                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.profile_edit_save),
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {},
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                shape = RoundedCornerShape(30.dp),

                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,

                icon = {

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(72.dp)
                    ) {

                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                Icons.Default.Save,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                },

                title = {

                    Text(
                        text = stringResource(R.string.profile_edit_success_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },

                text = {

                    Text(
                        text = stringResource(R.string.profile_edit_success_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },

                confirmButton = {

                    Button(
                        onClick = {

                            showSuccessDialog = false
                            navigateToHome()
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape = RoundedCornerShape(16.dp),

                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp
                        ),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {

                        Text(
                            stringResource(R.string.profile_continue),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            )
        }
    }
}