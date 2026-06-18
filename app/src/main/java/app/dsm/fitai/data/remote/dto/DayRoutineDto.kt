package app.dsm.fitai.data.remote.dto

data class DayRoutineDto(
    val dayName: String,
    val exercises: List<ExerciseDto>
)