package app.dsm.fitai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.dsm.fitai.data.local.database.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUser(uid: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clearAll()

    @Query("SELECT * FROM users WHERE syncPending = 1")
    suspend fun getPendingUsers(): List<UserEntity>

    @Query("UPDATE users SET syncPending = 0 WHERE uid = :uid")
    suspend fun markSynced(uid: String)
}