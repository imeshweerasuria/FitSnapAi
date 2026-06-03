package com.adheesha.fitsnapai.workout

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class WorkoutRepository(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    private val workoutsReference = database.reference.child("workout_sessions")

    suspend fun addWorkout(workout: WorkoutSession): WorkoutSession {
        val workoutId = workoutsReference
            .child(workout.userId)
            .push()
            .key ?: System.currentTimeMillis().toString()

        val workoutWithId = workout.copy(workoutId = workoutId)

        workoutsReference
            .child(workout.userId)
            .child(workoutId)
            .setValue(workoutWithId)
            .await()

        return workoutWithId
    }

    suspend fun getWorkoutsForUser(userId: String): List<WorkoutSession> {
        val snapshot = workoutsReference
            .child(userId)
            .get()
            .await()

        val workouts = mutableListOf<WorkoutSession>()

        snapshot.children.forEach { child ->
            val workout = child.getValue(WorkoutSession::class.java)

            if (workout != null) {
                workouts.add(workout)
            }
        }

        return workouts.sortedByDescending { it.createdAt }
    }

    suspend fun markWorkoutCompleted(
        userId: String,
        workoutId: String
    ) {
        val updates = mapOf<String, Any>(
            "completed" to true,
            "completedAt" to System.currentTimeMillis()
        )

        workoutsReference
            .child(userId)
            .child(workoutId)
            .updateChildren(updates)
            .await()
    }

    suspend fun deleteWorkout(
        userId: String,
        workoutId: String
    ) {
        workoutsReference
            .child(userId)
            .child(workoutId)
            .removeValue()
            .await()
    }
}