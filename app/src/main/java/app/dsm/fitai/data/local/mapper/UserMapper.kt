package app.dsm.fitai.data.local.mapper

import androidx.compose.ui.Modifier
import app.dsm.fitai.data.local.database.UserEntity
import app.dsm.fitai.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        uid = uid,
        name = name,
        lastName = lastName,
        birthDate = birthDate,
        sex = sex,
        weight = weight,
        objective = objective,
        level = level,
        trainingFrequency = trainingFrequency,
        trainingDuration = trainingDuration
    )
}

fun User.toEntity(isSyncPending: Boolean = true): UserEntity {
    return UserEntity(
        uid = uid,
        name = name,
        lastName = lastName,
        birthDate = birthDate,
        sex = sex,
        weight = weight,
        objective = objective,
        level = level,
        trainingFrequency = trainingFrequency,
        trainingDuration = trainingDuration,
        syncPending = isSyncPending
    )
}