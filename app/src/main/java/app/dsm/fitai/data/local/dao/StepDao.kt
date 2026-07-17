package app.dsm.fitai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.dsm.fitai.data.local.database.StepRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {
    @Query("SELECT * FROM step_records WHERE date = :date")
    suspend fun getStepsByDate(date: String): StepRecordEntity?

    @Query("SELECT * FROM step_records WHERE date = :date")
    fun getStepsFlowByDate(date: String): Flow<StepRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSteps(stepRecord: StepRecordEntity)

    @Query("SELECT * FROM step_records ORDER BY date DESC LIMIT 7")
    fun getWeeklySteps(): Flow<List<StepRecordEntity>>
}
