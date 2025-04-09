package com.example.pavlov.viewmodels

import com.example.pavlov.models.Goal
import com.example.pavlov.models.GoalFrequency
import com.example.pavlov.models.PavlovDaysOfWeek

data class GoalsState(
    val pendingGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    val showPopup: Boolean = false,
    val isEditMode: Boolean = false,
    val newGoal: Goal = Goal(),
    val showTimePickerDialog: Boolean = false,
)