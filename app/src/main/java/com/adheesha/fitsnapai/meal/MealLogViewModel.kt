package com.adheesha.fitsnapai.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MealLogViewModel(
    private val repository: MealLogRepository = MealLogRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealLogUiState())
    val uiState: StateFlow<MealLogUiState> = _uiState

    fun loadMeals(userId: String) {
        if (userId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "User not found. Please login again."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val meals = repository.getMealsForUser(userId)
                val todayMeals = meals.filter { it.dateKey == getTodayDateKey() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    meals = todayMeals,
                    summary = calculateSummary(todayMeals)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load meals."
                )
            }
        }
    }

    fun addMeal(
        userId: String,
        foodName: String,
        mealType: String,
        caloriesText: String,
        proteinText: String,
        carbsText: String,
        fatText: String,
        notes: String
    ) {
        val validationMessage = validateInputs(
            userId = userId,
            foodName = foodName,
            mealType = mealType,
            caloriesText = caloriesText,
            proteinText = proteinText,
            carbsText = carbsText,
            fatText = fatText
        )

        if (validationMessage != null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = validationMessage
            )
            return
        }

        val meal = MealLog(
            userId = userId,
            foodName = foodName.trim(),
            mealType = mealType,
            calories = caloriesText.toInt(),
            proteinGrams = proteinText.toIntOrNull() ?: 0,
            carbsGrams = carbsText.toIntOrNull() ?: 0,
            fatGrams = fatText.toIntOrNull() ?: 0,
            notes = notes.trim(),
            dateKey = getTodayDateKey(),
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                repository.addMeal(meal)

                val updatedMeals = repository.getMealsForUser(userId)
                    .filter { it.dateKey == getTodayDateKey() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    meals = updatedMeals,
                    summary = calculateSummary(updatedMeals),
                    successMessage = "Meal added successfully."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to add meal."
                )
            }
        }
    }

    fun deleteMeal(userId: String, mealId: String) {
        if (userId.isBlank() || mealId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Unable to delete meal."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                repository.deleteMeal(userId, mealId)

                val updatedMeals = repository.getMealsForUser(userId)
                    .filter { it.dateKey == getTodayDateKey() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    meals = updatedMeals,
                    summary = calculateSummary(updatedMeals),
                    successMessage = "Meal deleted successfully."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to delete meal."
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    private fun validateInputs(
        userId: String,
        foodName: String,
        mealType: String,
        caloriesText: String,
        proteinText: String,
        carbsText: String,
        fatText: String
    ): String? {
        if (userId.isBlank()) return "User not found. Please login again."

        if (foodName.isBlank()) return "Food name is required."

        if (mealType.isBlank()) return "Please select meal type."

        val calories = caloriesText.toIntOrNull()
        if (calories == null || calories <= 0 || calories > 10000) {
            return "Enter valid calories."
        }

        val protein = proteinText.toIntOrNull() ?: 0
        if (protein < 0 || protein > 500) {
            return "Enter valid protein grams."
        }

        val carbs = carbsText.toIntOrNull() ?: 0
        if (carbs < 0 || carbs > 1000) {
            return "Enter valid carbs grams."
        }

        val fat = fatText.toIntOrNull() ?: 0
        if (fat < 0 || fat > 500) {
            return "Enter valid fat grams."
        }

        return null
    }

    private fun calculateSummary(meals: List<MealLog>): MealSummary {
        return MealSummary(
            totalCalories = meals.sumOf { it.calories },
            totalProteinGrams = meals.sumOf { it.proteinGrams },
            totalCarbsGrams = meals.sumOf { it.carbsGrams },
            totalFatGrams = meals.sumOf { it.fatGrams },
            mealCount = meals.size
        )
    }

    private fun getTodayDateKey(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
}