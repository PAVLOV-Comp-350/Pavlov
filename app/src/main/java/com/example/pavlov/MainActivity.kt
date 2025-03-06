package com.example.pavlov

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.WorkManager
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Create notification channel (Important for Android 8.0+)
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        setContent {
            PavlovApp(this) // ✅ Pass context to PavlovApp
        }
    }
}

@Composable
fun PavlovApp(context: ComponentActivity) {
    // Track treats count
    val treatsCount = remember { mutableStateOf(0) }

    // ✅ List of goals (with remember for recomposition)
    val sampleGoals = remember {
        mutableStateListOf(
            Goal(
                id = "1",
                title = "Morning Meditation",
                description = "15 minutes of mindfulness meditation",
                streak = 5
            ),
            Goal(
                id = "2",
                title = "Exercise",
                description = "30 minutes of cardio",
                isCompleted = false,
                streak = 12
            ),
            Goal(
                id = "3",
                title = "Read",
                description = "Read 20 pages of current book",
                streak = 3
            ),
            Goal(
                id = "4",
                title = "Drink Water",
                description = "Drink 8 glasses of water",
                streak = 8
            )
        )
    }

    // ✅ Theme management
    PavlovTheme(darkTheme = ThemeManager.isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            GoalsListScreen(
                goals = sampleGoals,
                treatsCount = treatsCount.value,
                onGoalCheckedChange = { goal, isChecked ->
                    val index = sampleGoals.indexOfFirst { it.id == goal.id }
                    if (index >= 0) {
                        sampleGoals[index] = sampleGoals[index].copy(isCompleted = isChecked)

                        // ✅ Only increase treats when first checking, NOT when unchecking
                        if (isChecked && !goal.isCompleted) {
                            treatsCount.value++
                        }
                    }
                },
                onTreatEarned = { treatsCount.value++ },
                onAddGoalClick = {
                    val newGoal = Goal(id = UUID.randomUUID().toString(), title = "New Goal")

                    sampleGoals.add(newGoal) // ✅ Corrected goal list reference

                    // ✅ Schedule a notification 60 minutes from now
                    scheduleGoalReminder(context, newGoal, 60)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PavlovApp(context = MainActivity()) // Pass dummy context for preview
}
