package app.dsm.fitai.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.viewmodel.ProfileSetupViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import app.dsm.fitai.R
import app.dsm.fitai.ui.theme.FitAITheme
import app.dsm.fitai.viewmodel.ProfileSetupUiState

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
        onObjectiveChange = viewModel::onObjectiveChange,
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
    onObjectiveChange: (String) -> Unit = {},
    onBirthDateSelected: (Long) -> Unit = {},
    onNextStep: () -> Unit = {},
    onPreviousStep: () -> Unit = {},
    onSaveProfile: () -> Unit = {},
    context: android.content.Context? = null
) {
    val primary = Color(0xFF00BFA6)
    val unselectedSurface = Color(0xFFE6E8EC)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(40.dp))

        LinearProgressIndicator(
            progress = { state.currentStep / 2f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = primary,
            trackColor = unselectedSurface,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Paso ${state.currentStep} de 2",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
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
                        onBirthDateSelected = onBirthDateSelected,
                        context = context,
                        primary = primary,
                        unselectedSurface = unselectedSurface
                    )
                    2 -> StepObjective(
                        state = state,
                        onObjectiveChange = onObjectiveChange,
                        primary = primary
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
                    border = BorderStroke(1.dp, primary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = primary)
                    Spacer(Modifier.size(8.dp))
                    Text("Atrás", color = primary)
                }
                Spacer(Modifier.size(16.dp))
            }

            Button(
                onClick = { if (state.currentStep < 2) onNextStep() else onSaveProfile() },
                modifier = Modifier
                    .height(52.dp)
                    .weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                } else {
                    val text = if (state.currentStep < 2) "Continuar" else "Finalizar"
                    Text(text, fontWeight = FontWeight.Bold)
                    if (state.currentStep < 2) {
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
    onBirthDateSelected: (Long) -> Unit,
    context: android.content.Context?,
    primary: Color,
    unselectedSurface: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Datos Personales",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = primary
            )
        )
        Text("Cuéntanos un poco sobre ti.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.firstName,
            onValueChange = onFirstNameChange,
            label = { Text("Nombres") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.lastName,
            onValueChange = onLastNameChange,
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Sexo",
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
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
                    containerColor = if (isMale) primary else unselectedSurface,
                    contentColor = if (isMale) Color.White else Color.Black
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Masculino")
            }

            Button(
                onClick = { onGenderChange("Femenino") },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFemale) primary else unselectedSurface,
                    contentColor = if (isFemale) Color.White else Color.Black
                ),
                modifier = Modifier.weight(1f)
            ) { Text("Femenino") }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Fecha de nacimiento",
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.birthDateText,
            onValueChange = {},
            label = { Text("Selecciona tu fecha") },
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
                }) { Icon(Icons.Default.CalendarMonth, null, tint = primary) }
            }
        )

        state.error?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StepObjective(
    state: ProfileSetupUiState,
    onObjectiveChange: (String) -> Unit,
    primary: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "¿Cuál es tu objetivo?",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = primary
            )
        )
        Text("Selecciona una opción para personalizar tu plan.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(24.dp))

        val objectives = listOf("Hipertrofia", "Fuerza", "Mixto")
        objectives.forEach { objective ->
            val isSelected = state.selectedObjective == objective
            OutlinedCard(
                onClick = { onObjectiveChange(objective) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                border = if (isSelected) BorderStroke(1.5.dp, primary) else CardDefaults.outlinedCardBorder(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) primary.copy(alpha = 0.1f) else Color.Transparent
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isSelected, onClick = { onObjectiveChange(objective) }, colors = RadioButtonDefaults.colors(selectedColor = primary))
                    Spacer(Modifier.size(12.dp))
                    Text(objective, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, style = MaterialTheme.typography.bodyLarge)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = when (objective) {
                                "Hipertrofia" -> "Enfoque en el crecimiento muscular estético y densidad mediante volumen optimizado."
                                "Fuerza" -> "Incrementa tu potencia máxima y fuerza mediante la capacidad de carga en ejercicios multiarticulares."
                                else -> "Equilibrio perfecto entre desarrollo muscular, fuerza y rendimiento en el entrenamiento cardiovascular."
                            },
                            modifier = Modifier.padding(10.dp)
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
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
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

//@Preview(showBackground = true, name = "Estado de Carga")
//@Composable
//fun ProfileSetupLoadingPreview() {
//    FitAITheme {
//        ProfileSetupContent(
//            state = ProfileSetupUiState(
//                currentStep = 2,
//                isLoading = true
//            )
//        )
//    }
//}
