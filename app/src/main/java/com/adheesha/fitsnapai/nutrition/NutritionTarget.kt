package com.adheesha.fitsnapai.nutrition

data class NutritionTarget(
    val userId: String = "",
    val bmr: Int = 0,
    val maintenanceCalories: Int = 0,
    val dailyCalories: Int = 0,
    val proteinGrams: Int = 0,
    val carbsGrams: Int = 0,
    val fatGrams: Int = 0,
    val fitnessGoal: String = "",
    val activityLevel: String = "",
    val currentWeightKg: Double = 0.0,
    val targetWeightKg: Double = 0.0,
    val calculatedAt: Long = 0L
)