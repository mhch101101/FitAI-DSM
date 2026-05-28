package app.dsm.fitai.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey
    val uid: String,

    val name: String,

    val lastName: String,

    val birthDate: Long,

    val sex: String,

    val weight: Float = 0f,

    val objective: String = ""

)