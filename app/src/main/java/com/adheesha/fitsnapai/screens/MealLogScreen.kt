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
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adheesha.fitsnapai.meal.MealLog
import com.adheesha.fitsnapai.meal.MealLogViewModel
import com.adheesha.fitsnapai.meal.MealSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MealLogScreen(
    userId: String,
    mealLogViewModel: MealLogViewModel,
    onBackClick: () -> Unit
) {
    val uiState by mealLogViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var foodName by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        mealLogViewModel.loadMeals(userId)
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        val message = uiState.errorMessage ?: uiState.successMessage

        if (message != null) {
            snackbarHostState.showSnackbar(message)
            mealLogViewModel.clearMessages()

            if (uiState.successMessage == "Meal added successfully.") {
                foodName = ""
                mealType = ""
                calories = ""
                protein = ""
                carbs = ""
                fat = ""
                notes = ""
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
                text = "Meal Log",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Track your meals and daily macros manually",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            TodaySummaryCard(summary = uiState.summary)

            Spacer(modifier = Modifier.height(20.dp))

            AddMealCard(
                foodName = foodName,
                onFoodNameChange = { foodName = it },
                mealType = mealType,
                onMealTypeChange = { mealType = it },
                calories = calories,
                onCaloriesChange = { calories = it },
                protein = protein,
                onProteinChange = { protein = it },
                carbs = carbs,
                onCarbsChange = { carbs = it },
                fat = fat,
                onFatChange = { fat = it },
                notes = notes,
                onNotesChange = { notes = it },
                isLoading = uiState.isLoading,
                onAddMealClick = {
                    mealLogViewModel.addMeal(
                        userId = userId,
                        foodName = foodName,
                        mealType = mealType,
                        caloriesText = calories,
                        proteinText = protein,
                        carbsText = carbs,
                        fatText = fat,
                        notes = notes
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Today's Meals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading && uiState.meals.isEmpty()) {
                LoadingMealsCard()
            } else if (uiState.meals.isEmpty()) {
                EmptyMealsCard()
            } else {
                uiState.meals.forEach { meal ->
                    MealItemCard(
                        meal = meal,
                        onDeleteClick = {
                            mealLogViewModel.deleteMeal(userId, meal.mealId)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
private fun TodaySummaryCard(summary: MealSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Today's Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${summary.totalCalories} kcal",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text("Protein ${summary.totalProteinGrams}g")
                    }
                )

                AssistChip(
                    onClick = { },
                    label = {
                        Text("Carbs ${summary.totalCarbsGrams}g")
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text("Fat ${summary.totalFatGrams}g")
                    }
                )

                AssistChip(
                    onClick = { },
                    label = {
                        Text("${summary.mealCount} meals")
                    }
                )
            }
        }
    }
}

@Composable
private fun AddMealCard(
    foodName: String,
    onFoodNameChange: (String) -> Unit,
    mealType: String,
    onMealTypeChange: (String) -> Unit,
    calories: String,
    onCaloriesChange: (String) -> Unit,
    protein: String,
    onProteinChange: (String) -> Unit,
    carbs: String,
    onCarbsChange: (String) -> Unit,
    fat: String,
    onFatChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    isLoading: Boolean,
    onAddMealClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Add Meal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = foodName,
                onValueChange = onFoodNameChange,
                label = {
                    Text("Food Name")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Meal Type",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            MealTypeChips(
                selectedMealType = mealType,
                onMealTypeSelected = onMealTypeChange
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = calories,
                onValueChange = onCaloriesChange,
                label = {
                    Text("Calories")
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = protein,
                    onValueChange = onProteinChange,
                    label = {
                        Text("Protein")
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = carbs,
                    onValueChange = onCarbsChange,
                    label = {
                        Text("Carbs")
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = fat,
                    onValueChange = onFatChange,
                    label = {
                        Text("Fat")
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = {
                    Text("Notes optional")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onAddMealClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Add Meal")
                }
            }
        }
    }
}

@Composable
private fun MealTypeChips(
    selectedMealType: String,
    onMealTypeSelected: (String) -> Unit
) {
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            mealTypes.take(2).forEach { type ->
                FilterChip(
                    selected = selectedMealType == type,
                    onClick = {
                        onMealTypeSelected(type)
                    },
                    label = {
                        Text(type)
                    }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            mealTypes.drop(2).forEach { type ->
                FilterChip(
                    selected = selectedMealType == type,
                    onClick = {
                        onMealTypeSelected(type)
                    },
                    label = {
                        Text(type)
                    }
                )
            }
        }
    }
}

@Composable
private fun MealItemCard(
    meal: MealLog,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = meal.foodName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = meal.mealType,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "${meal.calories} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text("P ${meal.proteinGrams}g")
                    }
                )

                AssistChip(
                    onClick = { },
                    label = {
                        Text("C ${meal.carbsGrams}g")
                    }
                )

                AssistChip(
                    onClick = { },
                    label = {
                        Text("F ${meal.fatGrams}g")
                    }
                )
            }

            if (meal.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = meal.notes,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatTime(meal.createdAt),
                style = MaterialTheme.typography.bodySmall
            )

            Divider(modifier = Modifier.padding(vertical = 10.dp))

            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Meal")
            }
        }
    }
}

@Composable
private fun LoadingMealsCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(12.dp))

            Text("Loading meals...")
        }
    }
}

@Composable
private fun EmptyMealsCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "No meals added today",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Add your first meal above to start tracking calories.")
        }
    }
}

private fun formatTime(timestamp: Long): String {
    if (timestamp == 0L) return "Time not available"

    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(Date(timestamp))
}