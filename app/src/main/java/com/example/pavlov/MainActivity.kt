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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PavlovApp()
        }
    }
}

@Composable
fun PavlovApp() {
    // Track treats count
    val treatsCount = remember { mutableStateOf(0) }

    // This will re-compose when ThemeManager.isDarkTheme changes
    PavlovTheme(darkTheme = ThemeManager.isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // ✅ Corrected list initialization
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

            GoalsListScreen(
                goals = sampleGoals,
                treatsCount = treatsCount.value,
                onGoalCheckedChange = { goal, isChecked ->
                    val index = sampleGoals.indexOfFirst { it.id == goal.id }
                    if (index >= 0) {
                        // ✅ Correctly update the list
                        sampleGoals[index] = sampleGoals[index].copy(isCompleted = isChecked)

                        // ✅ Only increase treats when first checking, NOT when unchecking
                        if (isChecked && !goal.isCompleted) {
                            treatsCount.value++
                        }
                    }
                },
                onTreatEarned = { treatsCount.value++ },
                onAddGoalClick = {
                    // ✅ Properly add a new goal
                    sampleGoals.add(
                        Goal(
                            id = (sampleGoals.size + 1).toString(),
                            title = "New Goal",
                            description = "Description for new goal"
                        )
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PavlovApp()
}
