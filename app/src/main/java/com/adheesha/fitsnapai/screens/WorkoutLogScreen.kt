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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adheesha.fitsnapai.workout.WorkoutExercise
import com.adheesha.fitsnapai.workout.WorkoutSession
import com.adheesha.fitsnapai.workout.WorkoutSummary
import com.adheesha.fitsnapai.workout.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutLogScreen(
    userId: String,
    workoutViewModel: WorkoutViewModel,
    onBackClick: () -> Unit
) {
    val uiState by workoutViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var workoutName by remember { mutableStateOf("") }
    var workoutType by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var exerciseName by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }

    val currentExercises = remember { mutableStateListOf<WorkoutExercise>() }

    LaunchedEffect(userId) {
        workoutViewModel.loadWorkouts(userId)
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        val message = uiState.errorMessage ?: uiState.successMessage

        if (message != null) {
            snackbarHostState.showSnackbar(message)
            workoutViewModel.clearMessages()

            if (uiState.successMessage == "Workout added successfully.") {
                workoutName = ""
                workoutType = ""
                notes = ""
                exerciseName = ""
                sets = ""
                reps = ""
                weightKg = ""
                currentExercises.clear()
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
                text = "Workout Log",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Track your gym workouts, exercises, sets, reps, and weight",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            WorkoutSummaryCard(summary = uiState.summary)

            Spacer(modifier = Modifier.height(20.dp))

            AddWorkoutCard(
                workoutName = workoutName,
                onWorkoutNameChange = { workoutName = it },
                workoutType = workoutType,
                onWorkoutTypeChange = { workoutType = it },
                notes = notes,
                onNotesChange = { notes = it },
                exerciseName = exerciseName,
                onExerciseNameChange = { exerciseName = it },
                sets = sets,
                onSetsChange = { sets = it },
                reps = reps,
                onRepsChange = { reps = it },
                weightKg = weightKg,
                onWeightKgChange = { weightKg = it },
                currentExercises = currentExercises,
                isLoading = uiState.isLoading,
                onAddExerciseClick = {
                    val validation = validateExerciseInput(
                        exerciseName = exerciseName,
                        sets = sets,
                        reps = reps,
                        weightKg = weightKg
                    )

                    if (validation != null) {
                        workoutViewModel.clearMessages()
                    } else {
                        currentExercises.add(
                            WorkoutExercise(
                                exerciseName = exerciseName.trim(),
                                sets = sets.toInt(),
                                reps = reps.toInt(),
                                weightKg = weightKg.toDoubleOrNull() ?: 0.0
                            )
                        )

                        exerciseName = ""
                        sets = ""
                        reps = ""
                        weightKg = ""
                    }
                },
                onRemoveExerciseClick = { exercise ->
                    currentExercises.remove(exercise)
                },
                onSaveWorkoutClick = {
                    workoutViewModel.addWorkout(
                        userId = userId,
                        workoutName = workoutName,
                        workoutType = workoutType,
                        exercises = currentExercises.toList(),
                        notes = notes
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Today's Workouts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading && uiState.workouts.isEmpty()) {
                LoadingWorkoutCard()
            } else if (uiState.workouts.isEmpty()) {
                EmptyWorkoutCard()
            } else {
                uiState.workouts.forEach { workout ->
                    WorkoutItemCard(
                        workout = workout,
                        onMarkCompletedClick = {
                            workoutViewModel.markCompleted(userId, workout.workoutId)
                        },
                        onDeleteClick = {
                            workoutViewModel.deleteWorkout(userId, workout.workoutId)
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

private fun validateExerciseInput(
    exerciseName: String,
    sets: String,
    reps: String,
    weightKg: String
): String? {
    if (exerciseName.isBlank()) return "Exercise name is required."

    val setCount = sets.toIntOrNull()
    if (setCount == null || setCount <= 0 || setCount > 50) {
        return "Enter valid sets."
    }

    val repCount = reps.toIntOrNull()
    if (repCount == null || repCount <= 0 || repCount > 500) {
        return "Enter valid reps."
    }

    val weight = weightKg.toDoubleOrNull() ?: 0.0
    if (weight < 0 || weight > 1000) {
        return "Enter valid weight."
    }

    return null
}

@Composable
private fun WorkoutSummaryCard(summary: WorkoutSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Today's Workout Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text("${summary.totalWorkouts} workouts")
                    }
                )

                AssistChip(
                    onClick = { },
                    label = {
                        Text("${summary.completedWorkouts} completed")
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
                        Text("${summary.totalExercises} exercises")
                    }
                )

                AssistChip(
                    onClick = { },
                    label = {
                        Text("${summary.totalSets} sets")
                    }
                )
            }
        }
    }
}

@Composable
private fun AddWorkoutCard(
    workoutName: String,
    onWorkoutNameChange: (String) -> Unit,
    workoutType: String,
    onWorkoutTypeChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    exerciseName: String,
    onExerciseNameChange: (String) -> Unit,
    sets: String,
    onSetsChange: (String) -> Unit,
    reps: String,
    onRepsChange: (String) -> Unit,
    weightKg: String,
    onWeightKgChange: (String) -> Unit,
    currentExercises: List<WorkoutExercise>,
    isLoading: Boolean,
    onAddExerciseClick: () -> Unit,
    onRemoveExerciseClick: (WorkoutExercise) -> Unit,
    onSaveWorkoutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Create Workout",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = workoutName,
                onValueChange = onWorkoutNameChange,
                label = {
                    Text("Workout Name")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Workout Type",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            WorkoutTypeChips(
                selectedWorkoutType = workoutType,
                onWorkoutTypeSelected = onWorkoutTypeChange
            )

            Spacer(modifier = Modifier.height(18.dp))

            Divider()

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Add Exercise",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = exerciseName,
                onValueChange = onExerciseNameChange,
                label = {
                    Text("Exercise Name")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = sets,
                    onValueChange = onSetsChange,
                    label = {
                        Text("Sets")
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = reps,
                    onValueChange = onRepsChange,
                    label = {
                        Text("Reps")
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = weightKg,
                    onValueChange = onWeightKgChange,
                    label = {
                        Text("Kg")
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onAddExerciseClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Exercise to Workout")
            }

            if (currentExercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Exercises Added",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                currentExercises.forEach { exercise ->
                    CurrentExerciseCard(
                        exercise = exercise,
                        onRemoveClick = {
                            onRemoveExerciseClick(exercise)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
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
                onClick = onSaveWorkoutClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Save Workout")
                }
            }
        }
    }
}

@Composable
private fun WorkoutTypeChips(
    selectedWorkoutType: String,
    onWorkoutTypeSelected: (String) -> Unit
) {
    val workoutTypes = listOf(
        "Push",
        "Pull",
        "Legs",
        "Full Body",
        "Cardio",
        "Custom"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            workoutTypes.take(3).forEach { type ->
                FilterChip(
                    selected = selectedWorkoutType == type,
                    onClick = {
                        onWorkoutTypeSelected(type)
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
            workoutTypes.drop(3).forEach { type ->
                FilterChip(
                    selected = selectedWorkoutType == type,
                    onClick = {
                        onWorkoutTypeSelected(type)
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
private fun CurrentExerciseCard(
    exercise: WorkoutExercise,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = exercise.exerciseName,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${exercise.sets} sets x ${exercise.reps} reps x ${exercise.weightKg} kg",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onRemoveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Remove")
            }
        }
    }
}

@Composable
private fun WorkoutItemCard(
    workout: WorkoutSession,
    onMarkCompletedClick: () -> Unit,
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
                        text = workout.workoutName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = workout.workoutType,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            if (workout.isCompleted) "Completed" else "Pending"
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            workout.exercises.forEach { exercise ->
                Text(
                    text = "• ${exercise.exerciseName}: ${exercise.sets} x ${exercise.reps} x ${exercise.weightKg}kg",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (workout.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = workout.notes,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Created: ${formatTime(workout.createdAt)}",
                style = MaterialTheme.typography.bodySmall
            )

            if (workout.isCompleted && workout.completedAt != 0L) {
                Text(
                    text = "Completed: ${formatTime(workout.completedAt)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp))

            if (!workout.isCompleted) {
                Button(
                    onClick = onMarkCompletedClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mark as Completed")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Workout")
            }
        }
    }
}

@Composable
private fun LoadingWorkoutCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(12.dp))

            Text("Loading workouts...")
        }
    }
}

@Composable
private fun EmptyWorkoutCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "No workouts added today",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Create your first workout above.")
        }
    }
}

private fun formatTime(timestamp: Long): String {
    if (timestamp == 0L) return "Time not available"

    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(Date(timestamp))
}