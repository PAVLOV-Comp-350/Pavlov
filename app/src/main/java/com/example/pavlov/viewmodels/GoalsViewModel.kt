package com.example.pavlov.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pavlov.PavlovApplication
import com.example.pavlov.models.Activity
import com.example.pavlov.models.ActivityDao
import com.example.pavlov.models.GoalDao
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
            GoalsEvent.AddGoal -> {
                Log.d("TODO:", "Implement Adding goals")
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
        }
    }
}