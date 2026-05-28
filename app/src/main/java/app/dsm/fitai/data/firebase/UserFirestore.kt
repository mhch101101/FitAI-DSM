package app.dsm.fitai.data.firebase

import app.dsm.fitai.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserFirestore @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("users")

    suspend fun createUser(user: User) {
        usersCollection.document(user.uid).set(user).await()
    }

    suspend fun getUser(uid: String): User? {
        val snapshot = usersCollection.document(uid).get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(User::class.java)
        } else null
    }

    suspend fun updateUser(user: User) {
        usersCollection.document(user.uid).set(user).await()
    }

    suspend fun userExists(uid: String): Boolean {
        val snapshot = usersCollection.document(uid).get().await()
        return snapshot.exists()
    }

}