package com.adheesha.fitsnapai.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadProfile(userId: String) {
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

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load profile."
                )
            }
        }
    }

    fun saveProfile(
        userId: String,
        fullName: String,
        ageText: String,
        gender: String,
        heightText: String,
        currentWeightText: String,
        targetWeightText: String,
        fitnessGoal: String,
        activityLevel: String,
        experienceLevel: String
    ) {
        val validationMessage = validateInputs(
            userId = userId,
            fullName = fullName,
            ageText = ageText,
            gender = gender,
            heightText = heightText,
            currentWeightText = currentWeightText,
            targetWeightText = targetWeightText,
            fitnessGoal = fitnessGoal,
            activityLevel = activityLevel,
            experienceLevel = experienceLevel
        )

        if (validationMessage != null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = validationMessage
            )
            return
        }

        val profile = FitnessProfile(
            userId = userId,
            fullName = fullName.trim(),
            age = ageText.toInt(),
            gender = gender,
            heightCm = heightText.toDouble(),
            currentWeightKg = currentWeightText.toDouble(),
            targetWeightKg = targetWeightText.toDouble(),
            fitnessGoal = fitnessGoal,
            activityLevel = activityLevel,
            experienceLevel = experienceLevel,
            isProfileCompleted = true
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isSaved = false,
                errorMessage = null,
                successMessage = null
            )

            try {
                repository.saveFitnessProfile(profile)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = true,
                    profile = profile,
                    successMessage = "Fitness profile saved successfully."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to save profile."
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null,
            isSaved = false
        )
    }

    private fun validateInputs(
        userId: String,
        fullName: String,
        ageText: String,
        gender: String,
        heightText: String,
        currentWeightText: String,
        targetWeightText: String,
        fitnessGoal: String,
        activityLevel: String,
        experienceLevel: String
    ): String? {
        if (userId.isBlank()) return "User not found. Please login again."

        if (fullName.isBlank()) return "Full name is required."

        val age = ageText.toIntOrNull()
        if (age == null || age < 13 || age > 100) {
            return "Enter a valid age between 13 and 100."
        }

        if (gender.isBlank()) return "Please select gender."

        val height = heightText.toDoubleOrNull()
        if (height == null || height < 80 || height > 250) {
            return "Enter a valid height in cm."
        }

        val currentWeight = currentWeightText.toDoubleOrNull()
        if (currentWeight == null || currentWeight < 25 || currentWeight > 300) {
            return "Enter a valid current weight in kg."
        }

        val targetWeight = targetWeightText.toDoubleOrNull()
        if (targetWeight == null || targetWeight < 25 || targetWeight > 300) {
            return "Enter a valid target weight in kg."
        }

        if (fitnessGoal.isBlank()) return "Please select fitness goal."
        if (activityLevel.isBlank()) return "Please select activity level."
        if (experienceLevel.isBlank()) return "Please select experience level."

        return null
    }
}