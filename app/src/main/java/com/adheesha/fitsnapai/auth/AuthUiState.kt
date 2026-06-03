package com.adheesha.fitsnapai.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userEmail: String? = null,
    val userId: String? = null,
    val isPremium: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)