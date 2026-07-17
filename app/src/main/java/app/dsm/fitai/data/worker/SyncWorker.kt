package app.dsm.fitai.data.worker

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.dsm.fitai.data.local.entities.AppDatabase
import app.dsm.fitai.data.remote.firestore.FirestoreDay
import app.dsm.fitai.data.remote.firestore.FirestoreExercise
import app.dsm.fitai.data.remote.firestore.FirestoreRoutine
import app.dsm.fitai.data.remote.mapper.FirestoreMapper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.getValue

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(
    context,
    params
) {
    private val db by lazy {
        Room.databaseBuilder(applicationContext,AppDatabase::class.java,"fitai_db").build()
    }
    //private val db = AppDatabase.getDatabase(applicationContext)

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {

        return try {

            syncUsers()

            syncRoutines()

            Result.success()

        } catch (e: Exception) {

            e.printStackTrace()

            Result.retry()
        }
    }

    private suspend fun syncUsers() {

        val users = db.userDao().getPendingUsers()

        for (user in users) {

            val firestoreUser =
                FirestoreMapper.toFirestoreUser(user)

            firestore.collection("users")
                .document(user.uid)
                .set(firestoreUser)
                .await()

            db.userDao().markSynced(user.uid)
        }
    }

    private suspend fun syncRoutines() {

        val routines = db.routineDao().getPendingRoutines()

        for (routine in routines) {

            val days =
                db.routineDao()
                    .getRoutineDays(routine.id)

            val firestoreDays =
                mutableListOf<FirestoreDay>()

            for (day in days) {

                val routineExercises =
                    db.routineDao()
                        .getRoutineExercises(day.id)

                val firestoreExercises =
                    mutableListOf<FirestoreExercise>()

                for (routineExercise in routineExercises) {

                    val exercise =
                        db.exerciseDao()
                            .getExerciseById(
                                routineExercise.exerciseId
                            ) ?: continue

                    firestoreExercises.add(

                        FirestoreExercise(

                            name = exercise.name,

                            muscleGroup = exercise.bodyPart,

                            sets = routineExercise.targetSets,

                            reps = routineExercise.targetReps,

                            restTime = routineExercise.restTime,

                            weight = routineExercise.suggestedWeight
                        )
                    )
                }

                firestoreDays.add(

                    FirestoreDay(

                        dayName = day.dayName,

                        exercises = firestoreExercises
                    )
                )
            }

            val firestoreRoutine =
                FirestoreRoutine(

                    userId = routine.userId,

                    name = routine.name,

                    objective = routine.objective,

                    active = routine.isActive,

                    createdAt = routine.createdAt,

                    days = firestoreDays
                )

            firestore.collection("routines")
                .document(routine.id)
                .set(firestoreRoutine)
                .await()

            db.routineDao()
                .markRoutineSynced(routine.id)
        }
    }

}