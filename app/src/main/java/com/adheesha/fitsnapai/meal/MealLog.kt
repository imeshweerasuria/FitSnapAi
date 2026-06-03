package com.adheesha.fitsnapai.meal

data class MealLog(
    val mealId: String = "",
    val userId: String = "",
    val foodName: String = "",
    val mealType: String = "",
    val calories: Int = 0,
    val proteinGrams: Int = 0,
    val carbsGrams: Int = 0,
    val fatGrams: Int = 0,
    val notes: String = "",
    val dateKey: String = "",
    val createdAt: Long = 0L
)