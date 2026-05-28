package app.dsm.fitai.data.repository

import app.dsm.fitai.data.firebase.UserFirestore
import app.dsm.fitai.data.local.dao.UserDao
import app.dsm.fitai.data.local.mapper.toDomain
import app.dsm.fitai.data.local.mapper.toEntity
import app.dsm.fitai.domain.model.User
import app.dsm.fitai.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dao: UserDao,
    private val firestore: UserFirestore
) : UserRepository {

    override suspend fun getUser(uid: String): User? {
        val local = dao.getUser(uid)
        if (local != null) {
            return local.toDomain()
        }
        /*val remote = firestore.getUser(uid)
        if (remote != null) {
            dao.insertUser(remote.toEntity())
            return remote
        }*/
        return null
    }

    override suspend fun saveUserProfileInit(user: User) {
        dao.insertUser(user.toEntity())
        //firestore.createUser(user)
    }
}