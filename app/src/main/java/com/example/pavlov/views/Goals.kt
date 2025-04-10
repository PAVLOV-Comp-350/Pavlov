package com.example.pavlov.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import com.example.pavlov.models.Goal
import com.example.pavlov.viewmodels.GoalsEvent
import com.example.pavlov.viewmodels.GoalsState
import com.example.pavlov.models.PavlovDayOfWeek
import com.example.pavlov.models.PavlovDaysOfWeek
import com.example.pavlov.utils.Vec2
import com.example.pavlov.utils.plus
import com.example.pavlov.viewmodels.AnyEvent
import com.example.pavlov.viewmodels.SharedEvent
import com.example.pavlov.viewmodels.SharedState



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
    sharedState: SharedState,
    onEvent: (AnyEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
) {

    Scaffold(
        topBar = { PavlovTopBar(sharedState, onEvent = { onEvent(it) }) },
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
        },
        bottomBar = { PavlovNavbar(activeScreen = sharedState.activeScreen, onNavigate = onNavigate) },
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            // Your XP Bar!
            XpBar(
                currentXp = state.xp,
                xpToNextLevel = 100,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )


            Spacer(modifier = Modifier.height(8.dp))

            if (state.pendingGoals.isEmpty() && state.completedGoals.isEmpty()) {
                EmptyGoalsDisplay()
            } else {
                GoalsList(
                    pendingGoals = state.pendingGoals,
                    completedGoals = state.completedGoals,
                    onEvent = onEvent,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }


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

/**
 * Shows all the goals in a scrollable list
 */
@Composable
fun GoalsList(
    pendingGoals: List<Goal>,
    completedGoals: List<Goal>,
    onEvent: (AnyEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pendingGoals) { goal ->
            GoalItem(
                goal = goal,
                completed = false,
                onEvent = onEvent,
            )
        }
        items(completedGoals) { goal ->
            GoalItem(
                goal = goal,
                completed = true,
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
    onEvent: (AnyEvent) -> Unit,
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
            if (!completed) {
                var spawnCollectablePos by remember { mutableStateOf(Vec2(0f)) }
                Checkbox(
                    checked = false,
                    onCheckedChange = {
                        onEvent(
                            GoalsEvent.MarkGoalComplete(goal.id)
                        )
                        onEvent(
                            SharedEvent.GenerateCollectableRewards(
                                spawnCollectablePos
                            )
                        )
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.secondary,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .onGloballyPositioned {
                            if (it.isAttached) {
                                val bounds = it.boundsInRoot()
                                val off = it.positionInRoot()
                                spawnCollectablePos = Vec2(
                                    x = (off.x + bounds.width / 2),
                                    y = (off.y + bounds.height / 2),
                                )
                            }
                        },
                )
            }

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
                        PavlovDayOfWeek.entries.forEach {
                            DayDot(it, goal.activeDays)
                        }
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
    day: PavlovDayOfWeek,
    activeDays: PavlovDaysOfWeek,
) {
    val isActive = activeDays.isDayActive(day)
    Surface(
        modifier = Modifier.size(18.dp),
        shape = CircleShape,
        // Use primary color for active days, muted color for inactive days
        color = if (isActive) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = day.abbrev,
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
