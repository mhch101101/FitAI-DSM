package app.dsm.fitai.data.remote.firestore

data class FirestoreUser(

    val uid: String = "",

    val name: String = "",

    val lastName: String = "",

    val birthDate: Long = 0L,

    val sex: String = "",

    val weight: Float = 0f,

    val objective: String = "",

    val profileIncomplete: Boolean = false
)