package app.dsm.fitai.data.remote.firestore

data class FirestoreExercise(

    val name: String = "",

    val muscleGroup: String = "",

    val sets: Int = 0,

    val reps: String = "",

    val restTime: Int = 0,

    val weight: Double = 0.0
)