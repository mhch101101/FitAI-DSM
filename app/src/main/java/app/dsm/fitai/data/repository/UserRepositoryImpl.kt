package app.dsm.fitai.data.repository

import android.util.Log
import androidx.compose.animation.defaultDecayAnimationSpec
import app.dsm.fitai.data.firebase.UserFirestore
import app.dsm.fitai.data.local.dao.UserDao
import app.dsm.fitai.data.local.database.UserEntity
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
        val remote = firestore.getUser(uid)
        if (remote != null) {
            dao.insertUser(remote.toEntity())
            return remote
        }
        return null
    }

    override suspend fun saveUserProfile(user: User): Boolean {
        try {
            var local = dao.getUser(user.uid)
            if(local==null){
                local= UserEntity(
                    uid = user.uid,
                    name = user.name,
                    lastName = user.lastName,
                    sex = user.sex,
                    birthDate = user.birthDate
                )
                dao.insertUser(local)
            }else{
                local.name=user.name
                local.lastName =user.lastName
                local.sex=user.sex
                local.birthDate=user.birthDate
                dao.updateUser(local)
            }
            return true;
        }catch (e: Exception){
            return false
        }
    }

    override suspend fun saveUserProfileInit(user: User) {
        dao.insertUser(user.toEntity())
        if (firestore.userExists(user.uid)) firestore.updateUser(user) else firestore.createUser(user)
    }

}