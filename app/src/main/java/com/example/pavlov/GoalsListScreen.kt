package com.example.pavlov

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Data class for the Goal model
 */
data class Goal(
    val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val streak: Int = 0
)

/**
 * Main screen for showing all the goals
 *
 * @param goals List of goals to show on screen
 * @param onGoalCheckedChange What happens when user checks a box
 * @param onAddGoalClick What happens when the + button gets clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsListScreen(
    goals: List<Goal>,
    onGoalCheckedChange: (Goal, Boolean) -> Unit = { _, _ -> },
    onAddGoalClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Pavlov",
                            style = MaterialTheme.typography.headlineMedium,
                            // Bolded
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp)) {
                        ThemeSwitch()
                    }
                },
                actions = {
                    // Empty actions area to balance the layout
                    Spacer(modifier = Modifier.width(72.dp))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddGoalClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Goal"
                )
            }
        }
    ) { paddingValues ->
        if (goals.isEmpty()) {
            EmptyGoalsDisplay(modifier = Modifier.padding(paddingValues))
        } else {
            GoalsList(
                goals = goals,
                onGoalCheckedChange = onGoalCheckedChange,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

/**
 * Shows all the goals in a scrollable list
 */
@Composable
fun GoalsList(
    goals: List<Goal>,
    onGoalCheckedChange: (Goal, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(goals) { goal ->
            GoalItem(
                goal = goal,
                onCheckedChange = { checked -> onGoalCheckedChange(goal, checked) }
            )
        }
    }
}

/**
 * This makes one of those goal card things
 */
@Composable
fun GoalItem(
    goal: Goal,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = goal.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.secondary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (goal.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Show streak if available
                if (goal.streak > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Streak: ${goal.streak} days",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

/**
 * This shows up when you don't have any goals yet
 */
@Composable
fun EmptyGoalsDisplay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No goals yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hit that + button to add some stuff",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Preview of the goals list screen.

@Preview(showBackground = true)
@Composable
fun GoalsListScreenPreview() {
    MaterialTheme {
        GoalsListScreen(
            goals = listOf(
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
                    isCompleted = true,
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
                    streak = 7
                )
            )
        )
    }
}

/**
 * Preview of the empty state for the goals list screen.
 */
@Preview(showBackground = true)
@Composable
fun EmptyGoalsListScreenPreview() {
    MaterialTheme {
        GoalsListScreen(goals = emptyList())
    }
}
 */