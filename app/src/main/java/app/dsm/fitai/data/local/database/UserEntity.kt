package app.dsm.fitai.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey
    val uid: String,

    var name: String,

    var lastName: String,

    var birthDate: Long,

    var sex: String,

    var weight: Float = 0f,

    var objective: String = "",

    val syncPending:Boolean = true

)