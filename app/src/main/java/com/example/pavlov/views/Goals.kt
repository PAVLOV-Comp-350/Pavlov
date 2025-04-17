package com.example.pavlov.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pavlov.models.Goal
import com.example.pavlov.models.PavlovDayOfWeek
import com.example.pavlov.models.PavlovDaysOfWeek
import com.example.pavlov.utils.Vec2
import com.example.pavlov.viewmodels.AnyEvent
import com.example.pavlov.viewmodels.GoalsEvent
import com.example.pavlov.viewmodels.GoalsState
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
                    imageVector = Icons.Default.Add, contentDescription = "Add Goal"
                )
            }
        },
        bottomBar = {
            PavlovNavbar(
                activeScreen = sharedState.activeScreen,
                onNavigate = onNavigate
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.pendingGoals.isEmpty() && state.completedGoals.isEmpty()) {
                EmptyGoalsDisplay()
            } else {
                GoalsList(
                    pendingGoals = state.pendingGoals,
                    completedGoals = state.completedGoals,
                    expandedGoals = state.expandedGoals,
                    onEvent = onEvent,
                )
            }
        }
    }


    if (state.showPopup) {
        GoalAddPopup(onDismiss = { onEvent(GoalsEvent.HideAddGoalAlert) },
            onConfirm = { onEvent(GoalsEvent.ConfirmAddGoal) },
            state = state,
            onEvent = onEvent
        )
    } else {
        onEvent(GoalsEvent.HideAddGoalAlert)
    }
}

@Composable
fun GoalsList(
    pendingGoals: List<Goal>,
    completedGoals: List<Goal>,
    expandedGoals: Set<Int>,
    onEvent: (AnyEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    GoalContainer(
        title = "Daily Tasks",
        goals = pendingGoals,
        expandedGoals = expandedGoals,
        onEvent = onEvent
    )
    GoalContainer(
        title = "Completed Tasks",
        goals = completedGoals,
        expandedGoals = expandedGoals,
        onEvent = onEvent,
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        toggleable = true,
    )
}

@Composable
fun GoalContainer(
    title: String?,
    goals: List<Goal>,
    expandedGoals: Set<Int>,
    onEvent: (AnyEvent) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    toggleable: Boolean = false,
) {
    if(goals.isNotEmpty()) {
        Column(
            modifier = modifier
                .padding(horizontal = 4.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            var expanded by remember { mutableStateOf(false) }
            if(toggleable) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    title?.let {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    IconButton(onClick = {expanded = !expanded}) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Edit goal",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                AnimatedVisibility(expanded) {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        items(goals) { goal ->
                            GoalItem(
                                goal = goal,
                                expanded = expandedGoals.contains(goal.id),
                                completed = true,
                                onEvent = onEvent,
                            )
                        }
                    }
                }
            } else {
                title?.let {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.wrapContentHeight()
                ) {
                    items(goals) { goal ->
                        GoalItem(
                            goal = goal,
                            expanded = expandedGoals.contains(goal.id),
                            completed = false,
                            onEvent = onEvent,
                        )
                    }
                }
            }

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
    expanded: Boolean,
    onEvent: (AnyEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // This allows you to trigger an animation as soon as the AnimatedVisibility is added to the composition tree.
    val state = remember {
        MutableTransitionState(debugInitialState).apply {
            // Start the animation immediately.
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = state,
        enter =  expandVertically(),
        exit = shrinkVertically(),
    ) {
    Card(
        modifier = modifier
            .animateContentSize()
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onEvent(GoalsEvent.ExpandGoalItem(goal.id)) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            val titlePad = if(!expanded) 8.dp else 0.dp
            val titleColor = if(completed)
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.onSurface
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleLarge,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(titlePad)
            )
            AnimatedVisibility(expanded) {
                Column {
                    if (goal.description.isNotBlank()) {
                        Text(
                            text = goal.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!completed) {
                            var spawnCollectablePos by remember { mutableStateOf(Vec2(0f)) }
                            Button(
                                onClick = {
                                    onEvent(
                                        GoalsEvent.MarkGoalComplete(goal.id)
                                    )
                                    onEvent(
                                        GoalsEvent.ExpandGoalItem(goal.id)
                                    )
                                    onEvent(
                                        SharedEvent.GenerateCollectableRewards(
                                            spawnCollectablePos
                                        )
                                    )
                                    onEvent(
                                        SharedEvent.GainXpFromTask
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            ) {
                                Text(
                                    text = "Complete"
                                )
                            }
                        }

                        Column {

                            // Show streak if available
                            if (goal.streak > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Streak: ${goal.streak}x",
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
                                    val timeString =
                                        String.format("%d:%02d %s", displayHours, minutes, period)

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
                text = day.abbrev, style = MaterialTheme.typography.labelSmall,
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
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
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

// NOTE: Without this flag the goals are not able to be previewed due to animation weirdness
private const val debugInitialState = false
@Composable
@Preview
fun GoalsPreview() {
    val pending = listOf<Goal>(
        Goal(
            id = 0,
            title = "Walk The Dog",
            description = "Move them legs",
            streak = 10,
            activeDays = PavlovDaysOfWeek.ALL_DAYS,
        )
    )
    val completed = listOf<Goal>(
        Goal(
            id = 1,
            title = "Swim 10 Laps",
            description = "So wet...",
            streak = 4,
            activeDays = PavlovDaysOfWeek.ALL_DAYS,
        )
    )
    val expanded = setOf(0)

    val state = GoalsState(
        pendingGoals = pending,
        completedGoals = completed,
        expandedGoals = expanded,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        GoalsListScreen(
            state = state,
            sharedState = SharedState(),
            onEvent = {},
            onNavigate = {},
        )
    }
}

