package app.dsm.fitai.data.local.dao

import androidx.room.*
import app.dsm.fitai.data.local.database.TrainingLogEntity
import app.dsm.fitai.data.local.database.TrainingSessionEntity
import kotlinx.coroutines.flow.Flow

data class SetWithDate(
    val date: Long,
    @ColumnInfo(name = "weight_used") val weightUsed: Double,
    @ColumnInfo(name = "reps_performed") val repsPerformed: Int
)

@Dao
interface TrainingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrainingSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<TrainingLogEntity>)

    @Query(
        """
        SELECT s.date, l.weight_used, l.reps_performed
        FROM training_logs l
        JOIN training_sessions s ON l.training_session_id = s.id
        WHERE l.exercise_id = :exerciseId
        ORDER BY s.date ASC
        """
    )
    fun getSetsForExercise(exerciseId: String): Flow<List<SetWithDate>>

    @Query("SELECT DISTINCT exercise_id FROM training_logs")
    fun getLoggedExerciseIds(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM training_sessions")
    suspend fun countSessions(): Int
}
