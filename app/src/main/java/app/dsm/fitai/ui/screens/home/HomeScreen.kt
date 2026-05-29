package app.dsm.fitai.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.domain.model.DayRoutine
import app.dsm.fitai.domain.model.Exercise
import app.dsm.fitai.domain.model.Routine
import app.dsm.fitai.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navigateToLogin: () -> Unit = {},
    navigateToProfileEdit: () -> Unit = {}
) {
    val context = LocalContext.current
    val appComponent = (context.applicationContext as FitAIApp).appComponent

    val viewModel = remember {
        HomeViewModel(
            userRepository = appComponent.userRepository(),
            authRepository = appComponent.authRepository(),
            routineRepository = appComponent.routineRepository()
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    LayoutScreen(
        context = context,
        navigateToLogin = navigateToLogin,
        navigateToProfileEdit = navigateToProfileEdit
    ) {
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val routine = uiState.routine
            if (routine == null) {
                EmptyRoutineView(onCreateRoutine = { viewModel.createRoutine() })
            } else {
                RoutineView(routine)
            }
        }
    }
}

@Composable
fun EmptyRoutineView(onCreateRoutine: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
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

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onCreateRoutine,
            modifier = Modifier.size(200.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text("Crear Rutina", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "IA personalizada en segundos",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun RoutineView(routine: Routine) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = routine.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Objetivo: ${routine.objective}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(routine.days) { day ->
                DayCard(day)
            }
        }
    }
}

@Composable
fun DayCard(day: DayRoutine) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = day.dayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(Modifier.height(8.dp))
            
            day.exercises.forEach { exercise ->
                ExerciseItem(exercise)
                if (day.exercises.last() != exercise) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(exercise: Exercise) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${exercise.muscleGroup} • ${exercise.sets} series x ${exercise.reps}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (exercise.suggestedWeight > 0) {
                Text(
                    text = "Peso sugerido: ${exercise.suggestedWeight} kg",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Timer,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${exercise.restTime}s",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
