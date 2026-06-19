package app.dsm.fitai.data.local.mapper

import app.dsm.fitai.data.local.dao.RoutineWithDaysAndExercises
import app.dsm.fitai.data.local.database.*
import app.dsm.fitai.domain.model.DayRoutine
import app.dsm.fitai.domain.model.Exercise
import app.dsm.fitai.domain.model.Routine
import java.util.Date
import java.util.UUID

fun Routine.toEntity(): RoutineEntity {
    return RoutineEntity(
        id = this.id.ifEmpty { UUID.randomUUID().toString() },
        userId = this.userId,
        name = this.name,
        objective = this.objective,
        isActive = this.isActive,
        createdAt = this.createdAt?.time ?: System.currentTimeMillis(),
        isSynced = true
    )
}

fun Routine.toRoomStructure(routineId: String): Triple<List<RoutineDayEntity>, List<RoutineExerciseEntity>, List<ExerciseEntity>> {
    val dayEntities = mutableListOf<RoutineDayEntity>()
    val routineExerciseEntities = mutableListOf<RoutineExerciseEntity>()
    val catalogExercises = mutableListOf<ExerciseEntity>()

    this.days.forEach { day ->
        val dayId = UUID.randomUUID().toString()
        dayEntities.add(
            RoutineDayEntity(
                id = dayId,
                routineId = routineId,
                dayName = day.dayName
            )
        )

        day.exercises.forEach { exercise ->
            val exerciseId = exercise.name.lowercase().trim().replace(" ", "_")
            
            catalogExercises.add(
                ExerciseEntity(
                    id = exerciseId,
                    name = exercise.name,
                    bodyPart = exercise.muscleGroup
                )
            )

            routineExerciseEntities.add(
                RoutineExerciseEntity(
                    id = UUID.randomUUID().toString(),
                    routineDayId = dayId,
                    exerciseId = exerciseId,
                    targetSets = exercise.sets,
                    targetReps = exercise.reps,
                    suggestedWeight = exercise.suggestedWeight,
                    restTime = exercise.restTime
                )
            )
        }
    }

    return Triple(
        dayEntities, 
        routineExerciseEntities, 
        catalogExercises.distinctBy { it.id }
    )
}

fun RoutineWithDaysAndExercises.toDomain(): Routine {
    return Routine(
        id = this.routine.id,
        userId = this.routine.userId,
        name = this.routine.name,
        objective = this.routine.objective,
        isActive = this.routine.isActive,
        createdAt = Date(this.routine.createdAt),
        days = this.days.map { dayWithEx ->
            DayRoutine(
                dayName = dayWithEx.day.dayName,
                exercises = dayWithEx.exercises.map { exWithDetails ->
                    Exercise(
                        name = exWithDetails.exercise.name,
                        muscleGroup = exWithDetails.exercise.bodyPart,
                        sets = exWithDetails.routineExercise.targetSets,
                        reps = exWithDetails.routineExercise.targetReps,
                        restTime = exWithDetails.routineExercise.restTime,
                        suggestedWeight = exWithDetails.routineExercise.suggestedWeight
                    )
                }
            )
        }
    )
}

