package app.dsm.fitai.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.dsm.fitai.R
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.ui.theme.FitAITheme
import app.dsm.fitai.viewmodel.ProfileSetupUiState
import app.dsm.fitai.viewmodel.ProfileSetupViewModel

@Composable
fun ProfileSetupScreen(
    navigateToHome: () -> Unit = {}
) {

    val appComponent =
        (LocalContext.current.applicationContext as FitAIApp).appComponent

    val viewModel = remember {
        ProfileSetupViewModel(
            authRepository = appComponent.authRepository(),
            userRepository = appComponent.userRepository()
        )
    }

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) navigateToHome()
    }

    BackHandler(enabled = state.currentStep > 1) {
        viewModel.previousStep()
    }

    ProfileSetupContent(
        state = state,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onGenderChange = viewModel::onGenderChange,
        onWeightChange = viewModel::onWeightChange,
        onObjectiveChange = viewModel::onObjectiveChange,
        onLevelChange = viewModel::onLevelChange,
        onFrequencyChange = viewModel::onFrequencyChange,
        onDurationChange = viewModel::onDurationChange,
        onBirthDateSelected = viewModel::onBirthDateSelected,
        onNextStep = viewModel::nextStep,
        onPreviousStep = viewModel::previousStep,
        onSaveProfile = viewModel::saveProfile,
        context = context
    )
}

@Composable
fun ProfileSetupContent(
    state: ProfileSetupUiState,
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onGenderChange: (String) -> Unit = {},
    onWeightChange: (String) -> Unit = {},
    onObjectiveChange: (String) -> Unit = {},
    onLevelChange: (String) -> Unit = {},
    onFrequencyChange: (Int) -> Unit = {},
    onDurationChange: (Int) -> Unit = {},
    onBirthDateSelected: (Long) -> Unit = {},
    onNextStep: () -> Unit = {},
    onPreviousStep: () -> Unit = {},
    onSaveProfile: () -> Unit = {},
    context: android.content.Context? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(40.dp))

        LinearProgressIndicator(
            progress = { state.currentStep / 3f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.profile_step_indicator, state.currentStep),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedContent(
                targetState = state.currentStep,
                label = "StepTransition"
            ) { step ->
                when (step) {
                    1 -> StepPersonalData(
                        state = state,
                        onFirstNameChange = onFirstNameChange,
                        onLastNameChange = onLastNameChange,
                        onGenderChange = onGenderChange,
                        onWeightChange = onWeightChange,
                        onBirthDateSelected = onBirthDateSelected,
                        context = context
                    )
                    2 -> StepObjective(
                        state = state,
                        onObjectiveChange = onObjectiveChange
                    )
                    3 -> StepConfiguration(
                        state = state,
                        onLevelChange = onLevelChange,
                        onFrequencyChange = onFrequencyChange,
                        onDurationChange = onDurationChange
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.currentStep > 1) {
                OutlinedButton(
                    onClick = onPreviousStep,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(R.string.profile_back), color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.size(16.dp))
            }

            Button(
                onClick = { if (state.currentStep < 3) onNextStep() else onSaveProfile() },
                modifier = Modifier
                    .height(52.dp)
                    .weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    val textRes = if (state.currentStep < 3) R.string.profile_continue else R.string.profile_finish
                    Text(stringResource(textRes), fontWeight = FontWeight.Bold)
                    if (state.currentStep < 3) {
                        Spacer(Modifier.size(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                    }
                }
            }
        }
    }
}

@Composable
fun StepPersonalData(
    state: ProfileSetupUiState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onBirthDateSelected: (Long) -> Unit,
    context: android.content.Context?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.profile_personal_data_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(stringResource(R.string.profile_personal_data_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.firstName,
            onValueChange = onFirstNameChange,
            label = { Text(stringResource(R.string.profile_first_name)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.lastName,
            onValueChange = onLastNameChange,
            label = { Text(stringResource(R.string.profile_last_name)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.weight,
            onValueChange = onWeightChange,
            label = { Text(stringResource(R.string.profile_weight)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
            )
        )

        Spacer(Modifier.height(16.dp))

        Text(
            stringResource(R.string.profile_gender_label),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val isMale = state.gender == "Masculino"
            val isFemale = state.gender == "Femenino"
            Button(
                onClick = { onGenderChange("Masculino") },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMale) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isMale) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.profile_gender_male))
            }

            Button(
                onClick = { onGenderChange("Femenino") },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFemale) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isFemale) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) { Text(stringResource(R.string.profile_gender_female)) }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.profile_birth_date_label),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.birthDateText,
            onValueChange = {},
            label = { Text(stringResource(R.string.profile_birth_date_placeholder)) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                IconButton(onClick = {
                    val calendar = java.util.Calendar.getInstance()
                    android.app.DatePickerDialog(
                        context ?: return@IconButton,
                        { _, y, m, d ->
                            val cal = java.util.Calendar.getInstance()
                            cal.set(y, m, d)
                            onBirthDateSelected(cal.timeInMillis)
                        },
                        calendar.get(java.util.Calendar.YEAR),
                        calendar.get(java.util.Calendar.MONTH),
                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    ).show()
                }) { Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary) }
            }
        )

        state.error?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StepObjective(
    state: ProfileSetupUiState,
    onObjectiveChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.profile_objective_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(stringResource(R.string.profile_objective_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))

        val objectives = listOf("Hipertrofia", "Fuerza", "Mixto")
        objectives.forEach { objective ->
            val isSelected = state.selectedObjective == objective
            val objectiveNameRes = when(objective) {
                "Hipertrofia" -> R.string.profile_objective_hypertrophy
                "Fuerza" -> R.string.profile_objective_strength
                else -> R.string.profile_objective_mixed
            }
            OutlinedCard(
                onClick = { onObjectiveChange(objective) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onObjectiveChange(objective) },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(Modifier.size(12.dp))
                    Text(
                        stringResource(objectiveNameRes),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        val descRes = when (objective) {
                            "Hipertrofia" -> R.string.profile_objective_hypertrophy_desc
                            "Fuerza" -> R.string.profile_objective_strength_desc
                            else -> R.string.profile_objective_mixed_desc
                        }
                        Text(
                            text = stringResource(descRes),
                            modifier = Modifier.padding(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(8.dp))
                        Image(
                            painter = painterResource(
                                when (objective) {
                                    "Hipertrofia" -> R.drawable.hipertrofia
                                    "Fuerza" -> R.drawable.fuerza
                                    else -> R.drawable.mixto
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .padding(bottom = 10.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }
                }
            }
        }

        state.error?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun StepConfiguration(
    state: ProfileSetupUiState,
    onLevelChange: (String) -> Unit,
    onFrequencyChange: (Int) -> Unit,
    onDurationChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.profile_config_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = stringResource(R.string.profile_config_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))

        // Nivel de experiencia
        Text(stringResource(R.string.profile_experience_level), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Principiante", "Intermedio", "Avanzado").forEach { level ->
                val isSelected = state.selectedLevel == level
                val levelNameRes = when(level) {
                    "Principiante" -> R.string.profile_level_beginner
                    "Intermedio" -> R.string.profile_level_intermediate
                    else -> R.string.profile_level_advanced
                }
                Button(
                    onClick = { onLevelChange(level) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(stringResource(levelNameRes), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Días por semana
        Text(stringResource(R.string.profile_days_per_week), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.profile_days_per_week_subtitle), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (3..6).forEach { day ->
                val isSelected = state.trainingFrequency == day
                Button(
                    onClick = { onFrequencyChange(day) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(stringResource(R.string.profile_days_count, day), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Duración de sesión
        Text(stringResource(R.string.profile_session_duration), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.profile_session_duration_subtitle), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(45, 60, 90).forEach { mins ->
                val isSelected = state.trainingDuration == mins
                Button(
                    onClick = { onDurationChange(mins) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(stringResource(R.string.profile_session_minutes, mins), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }

        state.error?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true, name = "Paso 1 - Datos Personales")
@Composable
fun ProfileSetupStep1Preview() {
    FitAITheme {
        ProfileSetupContent(
            state = ProfileSetupUiState(
                currentStep = 1,
                firstName = "Juan",
                lastName = "Pérez"
            )
        )
    }
}

//@Preview(showBackground = true, name = "Paso 1 - Error")
//@Composable
//fun ProfileSetupStep1ErrorPreview() {
//    FitAITheme {
//        ProfileSetupContent(
//            state = ProfileSetupUiState(
//                currentStep = 1,
//                error = "La fecha de nacimiento es obligatoria"
//            )
//        )
//    }
//}

@Preview(showBackground = true, name = "Paso 2 - Objetivo")
@Composable
fun ProfileSetupStep2Preview() {
    FitAITheme {
        ProfileSetupContent(
            state = ProfileSetupUiState(
                currentStep = 2,
                selectedObjective = "Hipertrofia"
            )
        )
    }
}

@Preview(showBackground = true, name = "Paso 3 - Configuración")
@Composable
fun ProfileSetupStep3Preview() {
    FitAITheme {
        ProfileSetupContent(
            state = ProfileSetupUiState(
                currentStep = 3,
                selectedLevel = "Intermedio",
                trainingFrequency = 4,
                trainingDuration = 60
            )
        )
    }
}
