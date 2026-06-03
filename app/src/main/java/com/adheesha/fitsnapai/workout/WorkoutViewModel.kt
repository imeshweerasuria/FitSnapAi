package com.adheesha.fitsnapai.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutViewModel(
    private val repository: WorkoutRepository = WorkoutRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState

    fun loadWorkouts(userId: String) {
        if (userId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "User not found. Please login again."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val workouts = repository.getWorkoutsForUser(userId)
                val todayWorkouts = workouts.filter { it.dateKey == getTodayDateKey() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    workouts = todayWorkouts,
                    summary = calculateSummary(todayWorkouts)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load workouts."
                )
            }
        }
    }

    fun addWorkout(
        userId: String,
        workoutName: String,
        workoutType: String,
        exercises: List<WorkoutExercise>,
        notes: String
    ) {
        val validationMessage = validateWorkoutInputs(
            userId = userId,
            workoutName = workoutName,
            workoutType = workoutType,
            exercises = exercises
        )

        if (validationMessage != null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = validationMessage
            )
            return
        }

        val workout = WorkoutSession(
            userId = userId,
            workoutName = workoutName.trim(),
            workoutType = workoutType,
            exercises = exercises,
            notes = notes.trim(),
            isCompleted = false,
            dateKey = getTodayDateKey(),
            createdAt = System.currentTimeMillis(),
            completedAt = 0L
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                repository.addWorkout(workout)

                val updatedWorkouts = repository.getWorkoutsForUser(userId)
                    .filter { it.dateKey == getTodayDateKey() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    workouts = updatedWorkouts,
                    summary = calculateSummary(updatedWorkouts),
                    successMessage = "Workout added successfully."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to add workout."
                )
            }
        }
    }

    fun markCompleted(
        userId: String,
        workoutId: String
    ) {
        if (userId.isBlank() || workoutId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Unable to update workout."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                repository.markWorkoutCompleted(userId, workoutId)

                val updatedWorkouts = repository.getWorkoutsForUser(userId)
                    .filter { it.dateKey == getTodayDateKey() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    workouts = updatedWorkouts,
                    summary = calculateSummary(updatedWorkouts),
                    successMessage = "Workout marked as completed."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to update workout."
                )
            }
        }
    }

    fun deleteWorkout(
        userId: String,
        workoutId: String
    ) {
        if (userId.isBlank() || workoutId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Unable to delete workout."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                repository.deleteWorkout(userId, workoutId)

                val updatedWorkouts = repository.getWorkoutsForUser(userId)
                    .filter { it.dateKey == getTodayDateKey() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    workouts = updatedWorkouts,
                    summary = calculateSummary(updatedWorkouts),
                    successMessage = "Workout deleted successfully."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to delete workout."
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    private fun validateWorkoutInputs(
        userId: String,
        workoutName: String,
        workoutType: String,
        exercises: List<WorkoutExercise>
    ): String? {
        if (userId.isBlank()) return "User not found. Please login again."
        if (workoutName.isBlank()) return "Workout name is required."
        if (workoutType.isBlank()) return "Please select workout type."
        if (exercises.isEmpty()) return "Add at least one exercise."

        return null
    }

    private fun calculateSummary(workouts: List<WorkoutSession>): WorkoutSummary {
        val totalExercises = workouts.sumOf { it.exercises.size }
        val totalSets = workouts.sumOf { workout ->
            workout.exercises.sumOf { exercise -> exercise.sets }
        }

        return WorkoutSummary(
            totalWorkouts = workouts.size,
            completedWorkouts = workouts.count { it.isCompleted },
            totalExercises = totalExercises,
            totalSets = totalSets
        )
    }

    private fun getTodayDateKey(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
}