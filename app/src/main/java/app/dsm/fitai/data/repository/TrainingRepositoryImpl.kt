package app.dsm.fitai.data.repository

import app.dsm.fitai.data.local.dao.ExerciseDao
import app.dsm.fitai.data.local.dao.TrainingDao
import app.dsm.fitai.data.local.database.ExerciseEntity
import app.dsm.fitai.data.local.database.TrainingLogEntity
import app.dsm.fitai.data.local.database.TrainingSessionEntity
import app.dsm.fitai.domain.model.OneRmPoint
import app.dsm.fitai.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TrainingRepositoryImpl @Inject constructor(
    private val trainingDao: TrainingDao,
    private val exerciseDao: ExerciseDao
) : TrainingRepository {

    override fun getStrengthProgression(exerciseId: String): Flow<List<OneRmPoint>> {
        return trainingDao.getSetsForExercise(exerciseId).map { sets ->
            sets
                .groupBy { it.date }
                .map { (date, sessionSets) ->
                    // Epley: 1RM = peso * (1 + reps/30); se toma la mejor serie de la sesion.
                    val bestOneRm = sessionSets.maxOf { set ->
                        set.weightUsed * (1 + set.repsPerformed / 30.0)
                    }
                    OneRmPoint(dateMillis = date, oneRm = bestOneRm)
                }
                .sortedBy { it.dateMillis }
        }
    }

    override fun getLoggedExercises(): Flow<List<ExerciseEntity>> {
        return trainingDao.getLoggedExerciseIds().map { ids ->
            ids.mapNotNull { exerciseDao.getExerciseById(it) }
        }
    }

    override suspend fun seedDemoDataIfEmpty(userId: String) {
        if (trainingDao.countSessions() > 0) return

        val exercises = exerciseDao.getAllExercises().take(3).ifEmpty {
            val basics = listOf(
                ExerciseEntity(id = "seed_bench", name = "Press de banca", bodyPart = "Pecho"),
                ExerciseEntity(id = "seed_squat", name = "Sentadilla", bodyPart = "Piernas"),
                ExerciseEntity(id = "seed_deadlift", name = "Peso muerto", bodyPart = "Espalda")
            )
            exerciseDao.insertExercises(basics)
            basics
        }

        val baseWeights = listOf(50.0, 70.0, 90.0)
        val now = System.currentTimeMillis()
        val weeklyIncrement = 2.5

        exercises.forEachIndexed { index, exercise ->
            val baseWeight = baseWeights.getOrElse(index) { 50.0 }
            for (weeksAgo in 7 downTo 0) {
                val sessionDate = now - TimeUnit.DAYS.toMillis(weeksAgo * 7L)
                val session = TrainingSessionEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    routineDayId = null,
                    date = sessionDate,
                    durationMinutes = 60
                )
                trainingDao.insertSession(session)

                val sessionWeight = baseWeight + (7 - weeksAgo) * weeklyIncrement
                val logs = (1..3).map { setNumber ->
                    TrainingLogEntity(
                        id = UUID.randomUUID().toString(),
                        trainingSessionId = session.id,
                        exerciseId = exercise.id,
                        setNumber = setNumber,
                        repsPerformed = 8 + (weeksAgo + setNumber) % 5,
                        weightUsed = sessionWeight
                    )
                }
                trainingDao.insertLogs(logs)
            }
        }
    }
}
