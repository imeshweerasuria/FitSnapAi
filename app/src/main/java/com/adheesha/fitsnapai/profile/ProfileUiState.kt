package com.adheesha.fitsnapai.profile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val profile: FitnessProfile? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)