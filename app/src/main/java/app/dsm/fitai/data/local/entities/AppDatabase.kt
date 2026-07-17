package app.dsm.fitai.data.local.entities

import androidx.room.Database
import androidx.room.RoomDatabase
import app.dsm.fitai.data.local.dao.UserDao
import app.dsm.fitai.data.local.dao.RoutineDao
import app.dsm.fitai.data.local.dao.ExerciseDao
import app.dsm.fitai.data.local.dao.StepDao
import app.dsm.fitai.data.local.dao.TrainingDao
import app.dsm.fitai.data.local.database.*

@Database(
    entities = [
        UserEntity::class,
        RoutineEntity::class,
        RoutineDayEntity::class,
        ExerciseEntity::class,
        RoutineExerciseEntity::class,
        StepRecordEntity::class,
        TrainingSessionEntity::class,
        TrainingLogEntity::class
    ],
    version = 6,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun routineDao(): RoutineDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun stepDao(): StepDao
    abstract fun trainingDao(): TrainingDao
}
