package app.dsm.fitai.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dsm.fitai.data.local.database.ExerciseEntity
import app.dsm.fitai.domain.model.OneRmPoint
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.StepRepository
import app.dsm.fitai.domain.repository.TrainingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val stepRepository: StepRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _selectedExerciseId = MutableStateFlow<String?>(null)
    val selectedExerciseId: StateFlow<String?> = _selectedExerciseId.asStateFlow()

    val exercises: StateFlow<List<ExerciseEntity>> = trainingRepository.getLoggedExercises()
        .onEach { list ->
            if (_selectedExerciseId.value == null && list.isNotEmpty()) {
                _selectedExerciseId.value = list.first().id
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val oneRmPoints: StateFlow<List<OneRmPoint>> = _selectedExerciseId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else trainingRepository.getStrengthProgression(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklySteps = stepRepository.getWeeklyHistory()
        .map { it.sortedBy { record -> record.date } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            try {
                stepRepository.seedDemoStepsIfEmpty()
            } catch (e: Exception) {
                Log.e("ProgressViewModel", "Error sembrando datos demo de pasos", e)
            }

            val uid = authRepository.getCurrentUid()
            if (uid != null) {
                try {
                    trainingRepository.seedDemoDataIfEmpty(uid)
                } catch (e: Exception) {
                    Log.e("ProgressViewModel", "Error sembrando datos demo de entrenamiento", e)
                }
            }
        }
    }

    fun selectExercise(exerciseId: String) {
        _selectedExerciseId.value = exerciseId
    }
}
