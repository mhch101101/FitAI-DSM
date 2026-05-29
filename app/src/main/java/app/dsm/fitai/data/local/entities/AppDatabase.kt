package app.dsm.fitai.data.local.entities

import androidx.room.Database
import androidx.room.RoomDatabase
import app.dsm.fitai.data.local.dao.UserDao
import app.dsm.fitai.data.local.database.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

}