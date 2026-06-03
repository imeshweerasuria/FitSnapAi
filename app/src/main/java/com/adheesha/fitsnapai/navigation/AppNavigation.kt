package com.adheesha.fitsnapai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adheesha.fitsnapai.auth.AuthViewModel
import com.adheesha.fitsnapai.profile.ProfileViewModel
import com.adheesha.fitsnapai.screens.HomeScreen
import com.adheesha.fitsnapai.screens.LoginScreen
import com.adheesha.fitsnapai.screens.ProfileSetupScreen
import com.adheesha.fitsnapai.screens.RegisterScreen
import com.adheesha.fitsnapai.screens.SplashScreen
import kotlinx.coroutines.delay

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen()

            LaunchedEffect(Unit) {
                delay(1200)

                authViewModel.checkCurrentUser()

                val destination = if (authViewModel.uiState.value.isLoggedIn) {
                    Routes.HOME
                } else {
                    Routes.LOGIN
                }

                navController.navigate(destination) {
                    popUpTo(Routes.SPLASH) {
                        inclusive = true
                    }
                }
            }
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.PROFILE_SETUP) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                onProfileClick = {
                    navController.navigate(Routes.PROFILE_SETUP)
                },
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.PROFILE_SETUP) {
            val userId = authViewModel.uiState.value.userId ?: ""

            ProfileSetupScreen(
                userId = userId,
                profileViewModel = profileViewModel,
                onProfileSaved = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.PROFILE_SETUP) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}