package app.dsm.fitai.data.remote.dto

data class GenerateRoutineRequestDto(
    val objective: String,
    val experienceLevel: String,
    val daysPerWeek: Int,
    val sessionDuration: Int
)