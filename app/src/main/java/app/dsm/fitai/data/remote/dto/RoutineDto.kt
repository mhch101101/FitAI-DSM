package app.dsm.fitai.data.remote.dto

data class RoutineDto(
    val name: String,
    val objective: String,
    val days: List<DayRoutineDto>
)