package app.dsm.fitai.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color

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

    val primary = Color(0xFF00BFA6) // 🔥 teal fitness
    val softBackground = Color(0xFFF6F8FA)
    val selectedSurface = Color(0xFF00BFA6)
    val unselectedSurface = Color(0xFFE6E8EC)

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) navigateToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // ================= TITLE =================
        Text(
            text = "Completa tu perfil",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = primary
            )
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Personaliza tu experiencia fitness",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))

        // ================= INPUTS =================
        OutlinedTextField(
            value = state.firstName,
            onValueChange = viewModel::onFirstNameChange,
            label = { Text("Nombres") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primary,
                focusedLabelColor = primary
            )
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.lastName,
            onValueChange = viewModel::onLastNameChange,
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primary,
                focusedLabelColor = primary
            )
        )

        Spacer(Modifier.height(16.dp))

        // ================= SEXO =================
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
                onClick = { viewModel.onGenderChange("Masculino") },
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
                onClick = { viewModel.onGenderChange("Femenino") },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFemale) primary else unselectedSurface,
                    contentColor = if (isFemale) Color.White else Color.Black
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Femenino")
            }
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
            onValueChange = { },
            label = { Text("Selecciona tu fecha") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                IconButton(onClick = {
                    val calendar = java.util.Calendar.getInstance()

                    val datePicker = android.app.DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val cal = java.util.Calendar.getInstance()
                            cal.set(year, month, day)
                            viewModel.onBirthDateSelected(cal.timeInMillis)
                        },
                        calendar.get(java.util.Calendar.YEAR),
                        calendar.get(java.util.Calendar.MONTH),
                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    )

                    datePicker.show()
                }) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = primary
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primary,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(Modifier.height(24.dp))

        // ================= ERROR =================
        state.error?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = { viewModel.saveProfile()},
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primary
            ),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Guardar perfil",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}