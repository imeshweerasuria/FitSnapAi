package com.adheesha.fitsnapai.workout

data class WorkoutUiState(
    val isLoading: Boolean = false,
    val workouts: List<WorkoutSession> = emptyList(),
    val summary: WorkoutSummary = WorkoutSummary(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)