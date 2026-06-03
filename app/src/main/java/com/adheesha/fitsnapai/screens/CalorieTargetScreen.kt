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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adheesha.fitsnapai.nutrition.NutritionTarget
import com.adheesha.fitsnapai.nutrition.NutritionTargetViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CalorieTargetScreen(
    userId: String,
    nutritionTargetViewModel: NutritionTargetViewModel,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val uiState by nutritionTargetViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId) {
        nutritionTargetViewModel.loadNutritionTarget(userId)
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        val message = uiState.errorMessage ?: uiState.successMessage

        if (message != null) {
            snackbarHostState.showSnackbar(message)
            nutritionTargetViewModel.clearMessages()
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
                text = "Daily Calorie Target",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Calculate your calories and macros from your fitness profile",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                LoadingCard()
            } else {
                val target = uiState.target

                if (target == null) {
                    EmptyTargetCard(
                        onCalculateClick = {
                            nutritionTargetViewModel.calculateAndSaveTarget(userId)
                        },
                        onProfileClick = onProfileClick
                    )
                } else {
                    TargetSummaryCard(target = target)

                    Spacer(modifier = Modifier.height(20.dp))

                    MacroCard(target = target)

                    Spacer(modifier = Modifier.height(20.dp))

                    DetailsCard(target = target)

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            nutritionTargetViewModel.calculateAndSaveTarget(userId)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Recalculate Target")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
}

@Composable
private fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(16.dp))

            Text("Loading nutrition target...")
        }
    }
}

@Composable
private fun EmptyTargetCard(
    onCalculateClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "No target calculated yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Complete your fitness profile first, then generate your calorie and macro targets."
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onCalculateClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate My Target")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onProfileClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Fitness Profile")
            }
        }
    }
}

@Composable
private fun TargetSummaryCard(target: NutritionTarget) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Your Daily Target",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "${target.dailyCalories} kcal",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(target.fitnessGoal)
                    }
                )

                AssistChip(
                    onClick = { },
                    label = {
                        Text(target.activityLevel)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "This is your estimated daily calorie goal based on your profile."
            )
        }
    }
}

@Composable
private fun MacroCard(target: NutritionTarget) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Daily Macros",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            MacroRow(
                label = "Protein",
                value = "${target.proteinGrams}g",
                note = "${target.proteinGrams * 4} kcal"
            )

            Divider(modifier = Modifier.padding(vertical = 10.dp))

            MacroRow(
                label = "Carbs",
                value = "${target.carbsGrams}g",
                note = "${target.carbsGrams * 4} kcal"
            )

            Divider(modifier = Modifier.padding(vertical = 10.dp))

            MacroRow(
                label = "Fat",
                value = "${target.fatGrams}g",
                note = "${target.fatGrams * 9} kcal"
            )
        }
    }
}

@Composable
private fun MacroRow(
    label: String,
    value: String,
    note: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = label,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = note,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DetailsCard(target: NutritionTarget) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Calculation Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            DetailRow("BMR", "${target.bmr} kcal")
            DetailRow("Maintenance Calories", "${target.maintenanceCalories} kcal")
            DetailRow("Current Weight", "${target.currentWeightKg} kg")
            DetailRow("Target Weight", "${target.targetWeightKg} kg")
            DetailRow("Last Calculated", formatDate(target.calculatedAt))
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)

        Text(
            text = value,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "Not available"

    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return formatter.format(Date(timestamp))
}