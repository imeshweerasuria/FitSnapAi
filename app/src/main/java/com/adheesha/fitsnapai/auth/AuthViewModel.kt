package com.adheesha.fitsnapai.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        val user = repository.getCurrentUser()

        _uiState.value = _uiState.value.copy(
            isLoggedIn = user != null,
            userEmail = user?.email,
            userId = user?.uid,
            isPremium = false,
            isLoading = false,
            errorMessage = null
        )
    }

    fun register(email: String, password: String, confirmPassword: String) {
        if (!validateInputs(email, password)) return

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Passwords do not match."
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
                repository.registerUser(email.trim(), password)

                val user = repository.getCurrentUser()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    userEmail = user?.email,
                    userId = user?.uid,
                    isPremium = false,
                    successMessage = "Account created successfully."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Registration failed."
                )
            }
        }
    }

    fun login(email: String, password: String) {
        if (!validateInputs(email, password)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                repository.loginUser(email.trim(), password)

                val user = repository.getCurrentUser()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    userEmail = user?.email,
                    userId = user?.uid,
                    isPremium = false,
                    successMessage = "Login successful."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Login failed."
                )
            }
        }
    }

    fun logout() {
        repository.logoutUser()

        _uiState.value = AuthUiState(
            isLoggedIn = false,
            isPremium = false,
            successMessage = "Logged out successfully."
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email is required."
            )
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Enter a valid email address."
            )
            return false
        }

        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Password is required."
            )
            return false
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Password must be at least 6 characters."
            )
            return false
        }

        return true
    }
}