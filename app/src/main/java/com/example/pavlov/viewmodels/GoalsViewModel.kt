package com.example.pavlov.viewmodels

import androidx.compose.runtime.currentCompositionErrors
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pavlov.PavlovApplication
import com.example.pavlov.models.Activity
import com.example.pavlov.models.ActivityDao
import com.example.pavlov.models.Goal
import com.example.pavlov.models.GoalDao
import com.example.pavlov.models.PavlovDayOfWeek
import com.example.pavlov.utils.Vec2
import com.example.pavlov.utils.getRandomUnitVec2
import com.example.pavlov.utils.plus
import com.example.pavlov.utils.times
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class GoalsViewModel(
    private val goalDao: GoalDao,
    private val activityDao: ActivityDao
) : ViewModel() {
    // The internal state of the view model is private so that the UI can only
    // send updates to the state through the GoalsEvent Interface
    private val _state = MutableStateFlow(GoalsState())
    private val _activitiesCompletedToday = activityDao.getAllActivitiesCompletedToday()
    private val _allGoals = goalDao.getAllGoals()

    // State flow to filter only goals that are active today and have not been marked off on the activity log today
    private val _pendingGoals = combine(_activitiesCompletedToday, _allGoals) { activities, goals ->
        val today = PavlovDayOfWeek.today()
        goals.filter { goalIsPendingCompletion(it, activities, today) }
    }
    private val _completedGoals =
        combine(_activitiesCompletedToday, _allGoals) { activities, goals ->
            val today = PavlovDayOfWeek.today()
            goals.filter { !goalIsPendingCompletion(it, activities, today) }
        }

    private fun goalIsPendingCompletion(
        goal: Goal,
        activities: List<Activity>,
        today: PavlovDayOfWeek
    ): Boolean {
        if (!goal.activeDays.isDayActive(today)) return false;
        for (a in activities) {
            if (a.goalId == goal.id) { // The goal was already completed today
                return false;
            }
        }
        return true;
    }

    // Consumers of the GoalViewModel API subscribe to this StateFlow
    // to receive update to the UI state
    val state = combine(_state, _pendingGoals, _completedGoals) { state, pending, completed ->
        Unit
        state.copy(
            pendingGoals = pending,
            completedGoals = completed
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalsState())

    fun onEvent(event: GoalsEvent) {
        when (event) {
            GoalsEvent.ShowAddGoalAlert -> {
                _state.value = _state.value.copy(
                    showPopup = true,
                    isEditMode = false,
                    newGoal = Goal(),
                )
            }

            is GoalsEvent.SetGoalTitle -> {
                _state.update { current ->
                    current.copy(
                        newGoal = current.newGoal.copy(title = event.title)
                    )
                }
            }

            is GoalsEvent.SetGoalDescription -> {
                _state.update { current ->
                    current.copy(
                        newGoal = current.newGoal.copy(description = event.description)
                    )
                }
            }

            is GoalsEvent.SetGoalStreak -> {
                _state.update { current ->
                    current.copy(
                        newGoal = current.newGoal.copy(streak = event.streak)
                    )
                }
            }

            is GoalsEvent.ToggleGoalDay -> {
                _state.update { current ->
                    current.copy(
                        newGoal = current.newGoal.copy(
                            activeDays = current.newGoal.activeDays.toggleDay(event.day)
                        )
                    )
                }
            }

            is GoalsEvent.ConfirmAddGoal -> {
                viewModelScope.launch {
                    goalDao.addOrUpdateGoal(_state.value.newGoal)
                }
                _state.value = _state.value.copy(
                    showPopup = false
                )
            }

            GoalsEvent.HideAddGoalAlert -> {
                _state.value = _state.value.copy(
                    showPopup = false
                )
            }

            is GoalsEvent.ShowEditGoalAlert -> {
                val goalToEdit = findGoal { it.id == event.goalId }
                goalToEdit?.let {
                    _state.update {
                        it.copy(
                            showPopup = true,
                            isEditMode = true,
                            newGoal = goalToEdit,
                        )
                    }
                }
            }

            is GoalsEvent.DeleteGoal -> {
                val goalToDelete = findGoal { it.id == event.goalId }
                if (goalToDelete != null) {
                    viewModelScope.launch {
                        goalDao.removeGoal(goalToDelete)
                    }
                }
            }

            is GoalsEvent.MarkGoalComplete -> {

                viewModelScope.launch {
                    val goal = findGoal { it.id == event.goalId }
                    if (goal != null) {
                        val now = LocalDateTime.now()

                        val newStreak = calculateNewStreak(goal, now)

                        val bestStreak = if (goal.bestStreak > 0) {
                            maxOf(goal.bestStreak, newStreak)
                        } else {
                            newStreak
                        }

                        val streakBonus = calculateStreakBonus(newStreak)

                        activityDao.insertActivity(
                            Activity(
                                goalId = event.goalId,
                                completionTimestamp = now
                            )
                        )
                        goalDao.addOrUpdateGoal(
                            goal.copy(
                                streak = newStreak,
                                bestStreak = bestStreak,
                                lastCompletionDate = now,
                                totalCompletions = goal.totalCompletions + 1
                            )
                        )

                        PavlovApplication.addTreats(10 + streakBonus)

                        _state.update { current ->
                            current.copy(
                                xp = current.xp + 10 + streakBonus // You can change the base value as needed
                            )
                        }
                    }
                }
            }


            GoalsEvent.ShowTimePicker -> {
                _state.update {
                    it.copy(
                        showTimePickerDialog = true
                    )
                }
            }

            GoalsEvent.HideTimePicker -> {
                _state.update {
                    it.copy(
                        showTimePickerDialog = false
                    )
                }
            }

            is GoalsEvent.SetScheduledTime -> {
                _state.update { current ->
                    current.copy(
                        newGoal = current.newGoal.copy(scheduledTimeMinutes = event.minutes),
                        showTimePickerDialog = false,
                    )
                }
            }

            is GoalsEvent.ExpandGoalItem -> {
                _state.update { current ->
                    val mut = current.expandedGoals.toMutableSet()
                    if (current.expandedGoals.contains(event.goalId)) {
                        mut.remove(event.goalId)
                    } else {
                        mut.add(event.goalId)
                    }
                    current.copy(
                        expandedGoals = mut,
                    )
                }
            }
        }
    }

    /** Helper to search for a goal across all the different goal lists */
    private fun findGoal(predicate: (Goal) -> Boolean): Goal? {
        return state.value.pendingGoals.find(predicate) ?:
            state.value.completedGoals.find(predicate)
    }
    private fun calculateNewStreak(goal: Goal, completionTime: LocalDateTime): Int {
        if (goal.lastCompletionDate == null) {
            return 1
        }

        val lastCompletion = goal.lastCompletionDate
        val daysBetween = ChronoUnit.DAYS.between(
            lastCompletion.toLocalDate(),
            completionTime.toLocalDate()
        )

        return when {
            daysBetween == 0L -> goal.streak
            daysBetween == 1L -> goal.streak + 1
            else -> 1
        }
    }
    /**
     * Calculate bonus rewards based on the current streak length
     * Returns a multiplier value that increases with longer streaks
     */
    private fun calculateStreakBonus(streak: Int): Int {
        return when {
            streak >= 90 -> 25
            streak >= 30 -> 15
            streak >= 7 -> 8
            streak >= 3 -> 3
            else -> 1
        }
    }
}
