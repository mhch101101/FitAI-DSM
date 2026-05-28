package app.dsm.fitai.domain.model

import com.google.firebase.Timestamp

data class User(

    val uid: String = "",
    val name: String = "",
    val birthDate: Long = 0L,
    val sex: String = "",
    val weight: Float = 0f,
    val objective: String = ""

)