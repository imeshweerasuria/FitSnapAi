package com.adheesha.fitsnapai.nutrition

data class NutritionTargetUiState(
    val isLoading: Boolean = false,
    val target: NutritionTarget? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)