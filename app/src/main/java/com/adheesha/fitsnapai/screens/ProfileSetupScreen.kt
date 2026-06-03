package com.adheesha.fitsnapai.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adheesha.fitsnapai.profile.ProfileViewModel

@Composable
fun ProfileSetupScreen(
    userId: String,
    profileViewModel: ProfileViewModel,
    onProfileSaved: () -> Unit
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var heightCm by remember { mutableStateOf("") }
    var currentWeightKg by remember { mutableStateOf("") }
    var targetWeightKg by remember { mutableStateOf("") }
    var fitnessGoal by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var experienceLevel by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }

    LaunchedEffect(uiState.profile) {
        val profile = uiState.profile

        if (profile != null) {
            fullName = profile.fullName
            age = if (profile.age == 0) "" else profile.age.toString()
            gender = profile.gender
            heightCm = if (profile.heightCm == 0.0) "" else profile.heightCm.toString()
            currentWeightKg = if (profile.currentWeightKg == 0.0) "" else profile.currentWeightKg.toString()
            targetWeightKg = if (profile.targetWeightKg == 0.0) "" else profile.targetWeightKg.toString()
            fitnessGoal = profile.fitnessGoal
            activityLevel = profile.activityLevel
            experienceLevel = profile.experienceLevel
        }
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        val message = uiState.errorMessage ?: uiState.successMessage

        if (message != null) {
            snackbarHostState.showSnackbar(message)
            profileViewModel.clearMessages()

            if (uiState.successMessage != null) {
                onProfileSaved()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Fitness Profile",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Set up your body details and fitness goal",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Gender",
                        fontWeight = FontWeight.Bold
                    )

                    ChipRow(
                        options = listOf("Male", "Female", "Other"),
                        selectedOption = gender,
                        onOptionSelected = { gender = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = heightCm,
                        onValueChange = { heightCm = it },
                        label = { Text("Height (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = currentWeightKg,
                        onValueChange = { currentWeightKg = it },
                        label = { Text("Current Weight (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = targetWeightKg,
                        onValueChange = { targetWeightKg = it },
                        label = { Text("Target Weight (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Fitness Goal",
                        fontWeight = FontWeight.Bold
                    )

                    ChipRow(
                        options = listOf("Lose Fat", "Build Muscle", "Maintain Weight", "General Fitness"),
                        selectedOption = fitnessGoal,
                        onOptionSelected = { fitnessGoal = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Activity Level",
                        fontWeight = FontWeight.Bold
                    )

                    ChipColumn(
                        options = listOf(
                            "Sedentary",
                            "Lightly Active",
                            "Moderately Active",
                            "Very Active",
                            "Athlete"
                        ),
                        selectedOption = activityLevel,
                        onOptionSelected = { activityLevel = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Workout Experience",
                        fontWeight = FontWeight.Bold
                    )

                    ChipRow(
                        options = listOf("Beginner", "Intermediate", "Advanced"),
                        selectedOption = experienceLevel,
                        onOptionSelected = { experienceLevel = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            profileViewModel.saveProfile(
                                userId = userId,
                                fullName = fullName,
                                ageText = age,
                                gender = gender,
                                heightText = heightCm,
                                currentWeightText = currentWeightKg,
                                targetWeightText = targetWeightKg,
                                fitnessGoal = fitnessGoal,
                                activityLevel = activityLevel,
                                experienceLevel = experienceLevel
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator()
                        } else {
                            Text("Save Fitness Profile")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipRow(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selectedOption == option,
                onClick = {
                    onOptionSelected(option)
                },
                label = {
                    Text(option)
                }
            )
        }
    }
}

@Composable
private fun ChipColumn(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selectedOption == option,
                onClick = {
                    onOptionSelected(option)
                },
                label = {
                    Text(option)
                }
            )
        }
    }
}