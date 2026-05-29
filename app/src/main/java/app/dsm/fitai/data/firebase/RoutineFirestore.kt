package app.dsm.fitai.data.firebase

import app.dsm.fitai.domain.model.Routine
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RoutineFirestore @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val routinesCollection = firestore.collection("routines")

    suspend fun saveRoutine(routine: Routine) {
        routinesCollection.document(routine.userId).set(routine).await()
    }

    suspend fun getRoutine(userId: String): Routine? {
        val snapshot = routinesCollection.document(userId).get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(Routine::class.java)?.copy(id = snapshot.id)
        } else null
    }

    suspend fun updateRoutineStatus(userId: String, isActive: Boolean) {
        routinesCollection.document(userId).update("isActive", isActive).await()
    }
}
