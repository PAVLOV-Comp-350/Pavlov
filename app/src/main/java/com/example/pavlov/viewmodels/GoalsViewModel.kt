package com.example.pavlov.viewmodels


import androidx.compose.runtime.structuralEqualityPolicy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pavlov.models.Activity
import com.example.pavlov.models.ActivityDao
import com.example.pavlov.models.Goal
import com.example.pavlov.models.GoalDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
    val state = combine(_state, goalDao.getAllGoals()) { state, goals -> Unit
            state.copy(
                goals = goals
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalsState())

    fun onEvent(event: GoalsEvent) {
        when(event) {
            /**Divided the AddGoal event into 3-different events
             * the show/hide events will the popup while
             * the Confirm event will pass the new goal into the database
              */
            GoalsEvent.ShowAddGoalAlert -> {
                _state.value = _state.value.copy(
                    showPopup = true
                )
            }

            is GoalsEvent.ConfirmAddGoal -> {
                val newGoal = Goal(event.id, event.title, event.description, event.streak)
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

            is GoalsEvent.MarkGoalComplete -> {
                // NOTE(Devin): Temporary until we have a better way to mark goal completion
                val updatedCompletedGoals = _state.value.completedGoals.toMutableMap()
                val isComplete = updatedCompletedGoals[event.goalId] ?: false

                //toggle completion status
                updatedCompletedGoals[event.goalId] = !isComplete

                val newTreats = if (!isComplete) _state.value.totalTreats + 1 else _state.value.totalTreats

                _state.value = _state.value.copy(
                    completedGoals = updatedCompletedGoals,
                    totalTreats = newTreats
                )

                val activity = Activity(
                    goalId = event.goalId,
                    completionTimestamp = LocalDateTime.now()
                )
                viewModelScope.launch {
                    activityDao.insertActivity(activity)
                }
            }
        }
    }
}