package com.adheesha.fitsnapai.meal

data class MealLogUiState(
    val isLoading: Boolean = false,
    val meals: List<MealLog> = emptyList(),
    val summary: MealSummary = MealSummary(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)