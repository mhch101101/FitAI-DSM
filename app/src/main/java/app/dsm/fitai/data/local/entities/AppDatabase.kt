package app.dsm.fitai.data.local.entities

import androidx.room.Database
import androidx.room.RoomDatabase
import app.dsm.fitai.data.local.dao.UserDao
import app.dsm.fitai.data.local.dao.RoutineDao
import app.dsm.fitai.data.local.dao.ExerciseDao
import app.dsm.fitai.data.local.database.*

@Database(
    entities = [
        UserEntity::class,
        RoutineEntity::class,
        RoutineDayEntity::class,
        ExerciseEntity::class,
        RoutineExerciseEntity::class
    ],
    version = 5,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun routineDao(): RoutineDao
    abstract fun exerciseDao(): ExerciseDao
}
