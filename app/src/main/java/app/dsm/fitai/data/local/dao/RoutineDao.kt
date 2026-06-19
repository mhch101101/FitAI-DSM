package app.dsm.fitai.data.local.dao

import androidx.room.*
import app.dsm.fitai.data.local.database.ExerciseEntity
import app.dsm.fitai.data.local.database.RoutineEntity
import app.dsm.fitai.data.local.database.RoutineDayEntity
import app.dsm.fitai.data.local.database.RoutineExerciseEntity

@Dao
interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(days: List<RoutineDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<RoutineExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBaseExercises(exercises: List<ExerciseEntity>)

    @Query("UPDATE routines SET is_active = 0 WHERE user_id = :userId")
    suspend fun deactivatePreviousRoutines(userId: String)

    @Transaction
    suspend fun saveFullRoutine(
        routine: RoutineEntity,
        days: List<RoutineDayEntity>,
        exercises: List<RoutineExerciseEntity>,
        baseExercises: List<ExerciseEntity>
    ) {
        deactivatePreviousRoutines(routine.userId)
        insertBaseExercises(baseExercises)
        insertRoutine(routine.copy(isActive = true))
        insertDays(days)
        insertExercises(exercises)
    }

    @Transaction
    @Query("SELECT * FROM routines WHERE user_id = :userId AND is_active = 1 LIMIT 1")
    suspend fun getActiveRoutineWithDetails(userId: String): RoutineWithDaysAndExercises?
}

data class RoutineWithDaysAndExercises(
    @Embedded val routine: RoutineEntity,
    @Relation(
        entity = RoutineDayEntity::class,
        parentColumn = "id",
        entityColumn = "routine_id"
    )
    val days: List<DayWithExercises>
)

data class DayWithExercises(
    @Embedded val day: RoutineDayEntity,
    @Relation(
        entity = RoutineExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "routine_day_id"
    )
    val exercises: List<RoutineExerciseWithDetails>
)

data class RoutineExerciseWithDetails(
    @Embedded val routineExercise: RoutineExerciseEntity,
    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "id"
    )
    val exercise: ExerciseEntity
)
