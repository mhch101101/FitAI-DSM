package app.dsm.fitai.domain.repository

import app.dsm.fitai.domain.model.User

interface UserRepository {
    suspend fun getUser(uid: String): User?
    suspend fun saveUserProfileInit(user: User)
}