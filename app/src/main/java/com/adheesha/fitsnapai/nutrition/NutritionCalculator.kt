package com.adheesha.fitsnapai.nutrition

import com.adheesha.fitsnapai.profile.FitnessProfile
import kotlin.math.roundToInt

object NutritionCalculator {

    fun calculateTarget(profile: FitnessProfile): NutritionTarget {
        val bmr = calculateBmr(profile)
        val activityMultiplier = getActivityMultiplier(profile.activityLevel)
        val maintenanceCalories = (bmr * activityMultiplier).roundToInt()
        val dailyCalories = adjustCaloriesForGoal(
            maintenanceCalories = maintenanceCalories,
            fitnessGoal = profile.fitnessGoal
        )

        val proteinGrams = calculateProtein(profile)
        val fatGrams = calculateFat(dailyCalories)
        val carbsGrams = calculateCarbs(
            dailyCalories = dailyCalories,
            proteinGrams = proteinGrams,
            fatGrams = fatGrams
        )

        return NutritionTarget(
            userId = profile.userId,
            bmr = bmr,
            maintenanceCalories = maintenanceCalories,
            dailyCalories = dailyCalories,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams,
            fitnessGoal = profile.fitnessGoal,
            activityLevel = profile.activityLevel,
            currentWeightKg = profile.currentWeightKg,
            targetWeightKg = profile.targetWeightKg,
            calculatedAt = System.currentTimeMillis()
        )
    }

    private fun calculateBmr(profile: FitnessProfile): Int {
        val weight = profile.currentWeightKg
        val height = profile.heightCm
        val age = profile.age

        val bmr = when (profile.gender.lowercase()) {
            "male" -> {
                10 * weight + 6.25 * height - 5 * age + 5
            }

            "female" -> {
                10 * weight + 6.25 * height - 5 * age - 161
            }

            else -> {
                10 * weight + 6.25 * height - 5 * age - 78
            }
        }

        return bmr.roundToInt()
    }

    private fun getActivityMultiplier(activityLevel: String): Double {
        return when (activityLevel) {
            "Sedentary" -> 1.2
            "Lightly Active" -> 1.375
            "Moderately Active" -> 1.55
            "Very Active" -> 1.725
            "Athlete" -> 1.9
            else -> 1.2
        }
    }

    private fun adjustCaloriesForGoal(
        maintenanceCalories: Int,
        fitnessGoal: String
    ): Int {
        return when (fitnessGoal) {
            "Lose Fat" -> maintenanceCalories - 400
            "Build Muscle" -> maintenanceCalories + 300
            "Maintain Weight" -> maintenanceCalories
            "General Fitness" -> maintenanceCalories
            else -> maintenanceCalories
        }.coerceAtLeast(1200)
    }

    private fun calculateProtein(profile: FitnessProfile): Int {
        val multiplier = when (profile.fitnessGoal) {
            "Lose Fat" -> 2.0
            "Build Muscle" -> 2.0
            "Maintain Weight" -> 1.6
            "General Fitness" -> 1.5
            else -> 1.6
        }

        return (profile.currentWeightKg * multiplier).roundToInt()
    }

    private fun calculateFat(dailyCalories: Int): Int {
        val fatCalories = dailyCalories * 0.25
        return (fatCalories / 9).roundToInt()
    }

    private fun calculateCarbs(
        dailyCalories: Int,
        proteinGrams: Int,
        fatGrams: Int
    ): Int {
        val proteinCalories = proteinGrams * 4
        val fatCalories = fatGrams * 9
        val remainingCalories = dailyCalories - proteinCalories - fatCalories

        return (remainingCalories / 4).coerceAtLeast(0)
    }
}