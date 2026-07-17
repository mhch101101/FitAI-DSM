package app.dsm.fitai.data.remote.firestore

data class FirestoreDay(

    val dayName: String = "",

    val exercises: List<FirestoreExercise> = emptyList()
)