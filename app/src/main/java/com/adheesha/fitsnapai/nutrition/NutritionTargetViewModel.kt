package com.adheesha.fitsnapai.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NutritionTargetViewModel(
    private val repository: NutritionTargetRepository = NutritionTargetRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionTargetUiState())
    val uiState: StateFlow<NutritionTargetUiState> = _uiState

    fun loadNutritionTarget(userId: String) {
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
                val target = repository.getNutritionTarget(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    target = target
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load nutrition target."
                )
            }
        }
    }

    fun calculateAndSaveTarget(userId: String) {
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
                val profile = repository.getFitnessProfile(userId)

                if (profile == null || !profile.isProfileCompleted) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Please complete your fitness profile first."
                    )
                    return@launch
                }

                val target = NutritionCalculator.calculateTarget(profile)

                repository.saveNutritionTarget(target)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    target = target,
                    successMessage = "Daily calorie target calculated successfully."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to calculate nutrition target."
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
}