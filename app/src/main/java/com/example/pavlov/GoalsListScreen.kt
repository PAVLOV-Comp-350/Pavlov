package com.example.pavlov

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import android.content.Context
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import kotlin.random.Random
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.clickable
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import java.util.concurrent.TimeUnit

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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsListScreen(
    treatsCount: Int,
    goals: MutableList<Goal>,
    onGoalCheckedChange: (Goal, Boolean) -> Unit,
    onTreatEarned: () -> Unit,
    onAddGoalClick: () -> Unit = {}
) {
    var fallingTreats by remember { mutableStateOf(mutableListOf<Pair<Float, Boolean>>()) }

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
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = { ThemeSwitch() },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.dog_treat),
                            contentDescription = "Treats",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$treatsCount",
                            fontSize = 24.sp,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddGoalClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (goals.isEmpty()) {
                EmptyGoalsDisplay()
            } else {
                GoalsList(
                    goals = goals,
                    onGoalCheckedChange = { goal, isChecked ->
                        val index = goals.indexOfFirst { it.id == goal.id }
                        if (index >= 0) {
                            goals[index] = goals[index].copy(isCompleted = isChecked)

                            if (isChecked) {
                                fallingTreats = fallingTreats.toMutableList().apply {
                                    add(Pair(Random.nextFloat() * 300, false)) // Spawn treat with random X
                                }
                                onTreatEarned()
                            }
                        }
                    }
                )
            }

            // Draw falling treats
            fallingTreats.forEachIndexed { index, (startX, collected) ->
                if (!collected) {
                    FallingTreat(
                        startX = startX,
                        onCollected = {
                            fallingTreats = fallingTreats.toMutableList().apply {
                                set(index, Pair(startX, true)) // Mark as collected
                            }
                        },
                        onTreatEarned = onTreatEarned
                    )
                }
            }
        }
    }
}


/**
 * Shows all the goals in a scrollable list
 */
@Composable
fun GoalsList(
    goals: MutableList<Goal>,
    onGoalCheckedChange: (Goal, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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
                onCheckedChange = { checked -> onCheckedChange(checked) },
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
 * Falling treat animation - treats fall to the bottom and stay clickable
 */
@Composable
fun FallingTreat(
    startX: Float,
    onCollected: () -> Unit,
    onTreatEarned: () -> Unit
) {
    var isCollected by remember { mutableStateOf(false) }
    val treatY = remember { Animatable(0f) } // Explicitly setting Float type

    // Start the animation when the composable is first created
    LaunchedEffect(isCollected) {
        if (!isCollected) {
            treatY.animateTo(
                targetValue = 800f, // Fall to the bottom
                animationSpec = tween(
                    durationMillis = 2000, // 2 seconds to fall
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    if (!isCollected) {
        Icon(
            painter = painterResource(id = R.drawable.dog_treat),
            contentDescription = "Falling Treat",
            modifier = Modifier
                .offset(x = startX.dp, y = treatY.value.dp) // Apply animation
                .size(32.dp)
                .clickable {
                    isCollected = true
                    onCollected()
                    onTreatEarned()
                },
            tint = Color(0xFFFFD700) // Gold color
        )
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
                text = "Hit that + button to add some goals!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

fun scheduleGoalReminder(context: Context, goal: Goal, delayMinutes: Long) {
    val workManager = WorkManager.getInstance(context)

    val data = Data.Builder()
        .putString("goal_title", goal.title)
        .build()

    val reminderRequest = OneTimeWorkRequestBuilder<GoalReminderWorker>()
        .setInputData(data)
        .setInitialDelay(delayMinutes, TimeUnit.MINUTES) // Delay before notification
        .build()

    workManager.enqueue(reminderRequest)
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
