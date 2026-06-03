package com.adheesha.fitsnapai.profile

data class FitnessProfile(
    val userId: String = "",
    val fullName: String = "",
    val age: Int = 0,
    val gender: String = "",
    val heightCm: Double = 0.0,
    val currentWeightKg: Double = 0.0,
    val targetWeightKg: Double = 0.0,
    val fitnessGoal: String = "",
    val activityLevel: String = "",
    val experienceLevel: String = "",
    val isProfileCompleted: Boolean = false
)