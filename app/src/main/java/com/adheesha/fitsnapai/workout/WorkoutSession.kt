package com.adheesha.fitsnapai.workout

data class WorkoutSession(
    val workoutId: String = "",
    val userId: String = "",
    val workoutName: String = "",
    val workoutType: String = "",
    val exercises: List<WorkoutExercise> = emptyList(),
    val notes: String = "",
    val isCompleted: Boolean = false,
    val dateKey: String = "",
    val createdAt: Long = 0L,
    val completedAt: Long = 0L
)