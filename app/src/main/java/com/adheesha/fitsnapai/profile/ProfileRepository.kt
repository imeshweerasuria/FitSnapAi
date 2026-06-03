package com.adheesha.fitsnapai.profile

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    private val profilesReference = database.reference.child("fitness_profiles")

    suspend fun saveFitnessProfile(profile: FitnessProfile) {
        profilesReference
            .child(profile.userId)
            .setValue(profile)
            .await()
    }

    suspend fun getFitnessProfile(userId: String): FitnessProfile? {
        val snapshot = profilesReference
            .child(userId)
            .get()
            .await()

        return snapshot.getValue(FitnessProfile::class.java)
    }
}