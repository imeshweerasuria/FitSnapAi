package com.adheesha.fitsnapai.meal

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class MealLogRepository(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    private val mealLogsReference = database.reference.child("meal_logs")

    suspend fun addMeal(meal: MealLog): MealLog {
        val mealId = mealLogsReference
            .child(meal.userId)
            .push()
            .key ?: System.currentTimeMillis().toString()

        val mealWithId = meal.copy(mealId = mealId)

        mealLogsReference
            .child(meal.userId)
            .child(mealId)
            .setValue(mealWithId)
            .await()

        return mealWithId
    }

    suspend fun getMealsForUser(userId: String): List<MealLog> {
        val snapshot = mealLogsReference
            .child(userId)
            .get()
            .await()

        val meals = mutableListOf<MealLog>()

        snapshot.children.forEach { child ->
            val meal = child.getValue(MealLog::class.java)

            if (meal != null) {
                meals.add(meal)
            }
        }

        return meals.sortedByDescending { it.createdAt }
    }

    suspend fun deleteMeal(userId: String, mealId: String) {
        mealLogsReference
            .child(userId)
            .child(mealId)
            .removeValue()
            .await()
    }
}