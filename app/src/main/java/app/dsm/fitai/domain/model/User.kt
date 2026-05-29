package app.dsm.fitai.domain.model

import com.google.firebase.Timestamp

data class User(

    val uid: String = "",
    val weight: Float = 0f,
    val objective: String = "",

    val name: String = "",
    val lastName: String = "",
    val birthDate: Long = 0L,
    val sex: String = "",
    val level: String = "",
    val trainingFrequency: Int = 0,
    val trainingDuration: Int = 0
){
    fun isProfileIncomplete(): Boolean {
        return name.isBlank() ||
                lastName.isBlank() ||
                birthDate == 0L ||
                sex.isBlank() ||
                objective.isBlank() ||
                level.isBlank() ||
                trainingFrequency == 0 ||
                trainingDuration == 0
    }
}