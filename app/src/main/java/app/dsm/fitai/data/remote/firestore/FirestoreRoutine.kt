package app.dsm.fitai.data.remote.firestore

data class FirestoreRoutine(

    val userId: String = "",

    val name: String = "",

    val objective: String = "",

    val active: Boolean = true,

    val createdAt: Long = 0L,

    val days: List<FirestoreDay> = emptyList()
)