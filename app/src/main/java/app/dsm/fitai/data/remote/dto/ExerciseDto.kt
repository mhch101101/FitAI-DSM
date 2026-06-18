package app.dsm.fitai.data.remote.dto

data class ExerciseDto(
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val weight: Double
)