package app.dsm.fitai.domain.model

import java.util.Date

data class Routine(
    val id: String = "",           // Id Firestore
    val userId: String = "",
    val name: String = "",
    val objective: String = "",
    val isActive: Boolean = true,
    val createdAt: Date? = null,   // Mapeado de serverTimestamp
    val days: List<DayRoutine> = emptyList()
)

data class DayRoutine(
    val dayName: String = "",
    val exercises: List<Exercise> = emptyList()
)

data class Exercise(
    val name: String = "",
    val muscleGroup: String = "",
    val sets: Int = 0,
    val reps: String = "",
    val restTime: Int = 0,         // En segundos
    val suggestedWeight: Double = 0.0
)
