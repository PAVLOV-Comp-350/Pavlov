package com.example.pavlov.views

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
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import com.example.pavlov.models.DaysOfWeek
import com.example.pavlov.PavlovApplication
import com.example.pavlov.theme.ThemeSwitch
import com.example.pavlov.models.Goal
import com.example.pavlov.viewmodels.GoalsEvent
import com.example.pavlov.viewmodels.GoalsState
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.pavlov.R



/**
 * Main screen for showing all the goals
 *
 * @param state Immutable state passed in by the GoalsViewModel
 * @param onEvent Main callback for processing user interaction events
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsListScreen(
    state: GoalsState,
    onEvent: (GoalsEvent) -> Unit,
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.dog_treat),
                            contentDescription = "Total Treats",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.treats}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(GoalsEvent.ShowAddGoalAlert) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Goal"
                )
            }
        }
    ) { paddingValues ->
        if (state.goals.isEmpty()) {
            EmptyGoalsDisplay(modifier = Modifier.padding(paddingValues))
        } else {
            GoalsList(
                goals = state.goals,
                // NOTE(Devin): This is temporary until we decide on goal tracking
                completedGoals = state.completedGoals,
                onEvent = onEvent,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

    //If statement is used to trigger GoalAddPopup() and allowing the Popup to close when the showPopup value is set to "False"
    if (state.showPopup){
        GoalAddPopup(
            onDismiss = { onEvent(GoalsEvent.HideAddGoalAlert) },
            onConfirm = { onEvent(GoalsEvent.ConfirmAddGoal) },
            state = state,
            onEvent = onEvent
        )
    } else{
        onEvent(GoalsEvent.HideAddGoalAlert)
    }
}
@Composable
fun TreatsTracker(totalTreats: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(end = 16.dp)
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.dog_treat), // ⭐ Icon representing treats
            contentDescription = "Treats",
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = totalTreats.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}



/**
 * Shows all the goals in a scrollable list
 */
@Composable
fun GoalsList(
    goals: List<Goal>,
    // NOTE(Devin): This is temporary until we decide on goal tracking
    completedGoals: Map<Int, Boolean>,
    onEvent: (GoalsEvent) -> Unit,
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
                completed = completedGoals[goal.id] ?: false,
                onEvent = onEvent,
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
    completed: Boolean,
    onEvent: (GoalsEvent) -> Unit,
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
                checked = completed,
                onCheckedChange = { onEvent(GoalsEvent.MarkGoalComplete(goal.id))},
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Streak: ${goal.streak} days",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        val hours = goal.scheduledTimeMinutes / 60
                        val minutes = goal.scheduledTimeMinutes % 60
                        val displayHours = when {
                            hours == 0 -> 12
                            hours > 12 -> hours - 12
                            else -> hours
                        }
                        val period = if (hours < 12) "AM" else "PM"
                        val timeString = String.format("%d:%02d %s", displayHours, minutes, period)

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• $timeString",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                    //Display active days as small indicators
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Days: ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        DayDot("M", DaysOfWeek.MONDAY, goal.activeDays)
                        DayDot("T", DaysOfWeek.TUESDAY, goal.activeDays)
                        DayDot("W", DaysOfWeek.WEDNESDAY, goal.activeDays)
                        DayDot("TH", DaysOfWeek.THURSDAY, goal.activeDays)
                        DayDot("F", DaysOfWeek.FRIDAY, goal.activeDays)
                        DayDot("Sa", DaysOfWeek.SATURDAY, goal.activeDays)
                        DayDot("Su", DaysOfWeek.SUNDAY, goal.activeDays)
                    }

            }

            // Action buttons
            Row {
                IconButton(onClick = { onEvent(GoalsEvent.ShowEditGoalAlert(goal.id)) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit goal",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun DayDot(
    dayLabel: String,
    dayFlag: Int,
    activeDays: Int
) {
    val isActive = DaysOfWeek.isDayActive(activeDays, dayFlag)

    Surface(
        modifier = Modifier.size(18.dp),
        shape = androidx.compose.foundation.shape.CircleShape,
        // Use primary color for active days, muted color for inactive days
        color = if (isActive) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = dayLabel,
                style = MaterialTheme.typography.labelSmall,
                // Use onPrimary for active days, muted onSurface for inactive days
                color = if (isActive) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
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
