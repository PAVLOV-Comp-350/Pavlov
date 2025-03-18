package com.example.pavlov.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pavlov.PavlovApplication
import com.example.pavlov.models.Activity
import com.example.pavlov.models.ActivityDao
import com.example.pavlov.models.DaysOfWeek
import com.example.pavlov.models.Goal
import com.example.pavlov.models.GoalDao
import com.example.pavlov.models.GoalFrequency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class GoalsViewModel(
    private val goalDao: GoalDao,
    private val activityDao: ActivityDao
) : ViewModel() {
    // The internal state of the view model is private so that the UI can only
    // send updates to the state through the GoalsEvent Interface
    private val _state = MutableStateFlow(GoalsState())
    // Consumers of the GoalViewModel API subscribe to this StateFlow
    // to receive update to the UI state
    val state = combine(_state, goalDao.getAllGoals(), PavlovApplication.treats) { state, goals, treats -> Unit
            state.copy(
                goals = goals,
                treats = treats
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalsState())

    fun onEvent(event: GoalsEvent) {
        when(event) {

            GoalsEvent.ShowAddGoalAlert -> {
                _state.value = _state.value.copy(
                    showPopup = true,
                    isEditMode = false,
                    newGoalId = 0,
                    newGoalTitle = "",
                    newGoalDescription = "",
                    newGoalStreak = 0,
                    newGoalFrequency = GoalFrequency.DAILY,
                    newGoalSimple = false,
                    newGoalUnit = "No unit",
                    newGoalCurrent = 0,
                    newGoalTarget = 0,
                    newGoalActiveDays = 0,
                    newGoalScheduledTimeMinutes = 540,
                )
            }

            is GoalsEvent.SetGoalTitle -> {
                _state.value = _state.value.copy(
                    newGoalTitle = event.title
                )
            }

            is GoalsEvent.SetGoalDescription -> {
                _state.value = _state.value.copy(
                    newGoalDescription = event.description
                )
            }

            is GoalsEvent.SetGoalStreak -> {
                _state.value = _state.value.copy(
                    newGoalStreak = event.streak
                )
            }

            is GoalsEvent.ToggleGoalDay -> {
                val updatedDays = DaysOfWeek.toggleDay(_state.value.newGoalActiveDays, event.day)
                _state.value = _state.value.copy(
                    newGoalActiveDays = updatedDays
                )
            }

            is GoalsEvent.ConfirmAddGoal -> {
                val newGoal = Goal(
                    id =_state.value.newGoalId,
                    title = _state.value.newGoalTitle,
                    description = _state.value.newGoalDescription,
                    streak = _state.value.newGoalStreak,
                    frequency = _state.value.newGoalFrequency,
                    simple =_state.value.newGoalSimple,
                    unit = _state.value.newGoalUnit,
                    current = _state.value.newGoalCurrent,
                    target = _state.value.newGoalTarget,
                    activeDays = _state.value.newGoalActiveDays,
                    scheduledTimeMinutes = _state.value.newGoalScheduledTimeMinutes
                )
                viewModelScope.launch {
                    goalDao.addOrUpdateGoal(newGoal)
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
                val goalToEdit = state.value.goals.find { it.id == event.goalId }
                goalToEdit?.let {
                    _state.value = _state.value.copy(
                        showPopup = true,
                        isEditMode = true,
                        newGoalId = goalToEdit.id,
                        newGoalTitle = goalToEdit.title,
                        newGoalDescription = goalToEdit.description,
                        newGoalStreak = goalToEdit.streak,
                        newGoalFrequency = goalToEdit.frequency,
                        newGoalSimple = goalToEdit.simple,
                        newGoalUnit = goalToEdit.unit,
                        newGoalCurrent = goalToEdit.current,
                        newGoalTarget = goalToEdit.target,
                        newGoalActiveDays = goalToEdit.activeDays,
                        newGoalScheduledTimeMinutes = goalToEdit.scheduledTimeMinutes
                    )
                }
            }

            is GoalsEvent.DeleteGoal -> {
                val goalToDelete = state.value.goals.find { it.id == event.goalId }
                if (goalToDelete != null) {
                    viewModelScope.launch {
                        goalDao.removeGoal(goalToDelete)
                    }
                }
            }

            is GoalsEvent.MarkGoalComplete -> {
                // NOTE(Devin): Temporary until we have a better way to mark goal completion
                val updatedCompletedGoals = _state.value.completedGoals.toMutableMap()
                val isComplete = updatedCompletedGoals[event.goalId] ?: false

                //toggle completion status
                updatedCompletedGoals[event.goalId] = !isComplete

                PavlovApplication.addTreats(1)

                _state.value = _state.value.copy(
                    completedGoals = updatedCompletedGoals,
                )

                val activity = Activity(
                    goalId = event.goalId,
                    completionTimestamp = LocalDateTime.now()
                )
                viewModelScope.launch {
                    activityDao.insertActivity(activity)
                }
            }

            GoalsEvent.ShowTimePicker -> {
                _state.value = _state.value.copy(
                    showTimePickerDialog = true
                )
            }

            GoalsEvent.HideTimePicker -> {
                _state.value = _state.value.copy(
                    showTimePickerDialog = false
                )
            }

            is GoalsEvent.SetScheduledTime -> {
                _state.value = _state.value.copy(
                    newGoalScheduledTimeMinutes = event.minutes,
                    showTimePickerDialog = false
                )
            }
        }
    }
}
