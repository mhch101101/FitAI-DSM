package app.dsm.fitai.data.remote.mapper

import app.dsm.fitai.data.local.database.UserEntity
import app.dsm.fitai.data.remote.firestore.FirestoreUser

object FirestoreMapper {

    fun toFirestoreUser(user: UserEntity): FirestoreUser {

        return FirestoreUser(

            uid = user.uid,

            name = user.name,

            lastName = user.lastName,

            birthDate = user.birthDate,

            sex = user.sex,

            weight = user.weight,

            objective = user.objective,

            profileIncomplete = false
        )
    }
}