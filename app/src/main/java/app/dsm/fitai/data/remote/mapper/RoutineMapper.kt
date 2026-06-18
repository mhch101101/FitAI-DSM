package app.dsm.fitai.data.remote.mapper

import app.dsm.fitai.data.remote.dto.RoutineDto
import app.dsm.fitai.domain.model.DayRoutine
import app.dsm.fitai.domain.model.Exercise
import app.dsm.fitai.domain.model.Routine

fun RoutineDto.toDomain(): Routine {
    return Routine(
        userId = "",
        name = name,
        objective = objective,
        isActive = true,
        createdAt = java.util.Date(),
        days = days.map { day ->
            DayRoutine(
                dayName = day.dayName,
                exercises = day.exercises.map { exercise ->
                    Exercise(
                        name = exercise.name,
                        muscleGroup = exercise.muscleGroup,
                        sets = exercise.sets,
                        reps = exercise.reps,
                        restTime = exercise.restSeconds,
                        suggestedWeight = exercise.weight
                    )
                }
            )
        }
    )
}