package com.adheesha.fitsnapai.nutrition

import com.adheesha.fitsnapai.profile.FitnessProfile
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class NutritionTargetRepository(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    private val profilesReference = database.reference.child("fitness_profiles")
    private val targetsReference = database.reference.child("nutrition_targets")

    suspend fun getFitnessProfile(userId: String): FitnessProfile? {
        val snapshot = profilesReference
            .child(userId)
            .get()
            .await()

        return snapshot.getValue(FitnessProfile::class.java)
    }

    suspend fun saveNutritionTarget(target: NutritionTarget) {
        targetsReference
            .child(target.userId)
            .setValue(target)
            .await()
    }

    suspend fun getNutritionTarget(userId: String): NutritionTarget? {
        val snapshot = targetsReference
            .child(userId)
            .get()
            .await()

        return snapshot.getValue(NutritionTarget::class.java)
    }
}