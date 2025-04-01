package com.example.pavlov.viewmodels

import com.example.pavlov.models.Goal
import com.example.pavlov.models.GoalFrequency
import com.example.pavlov.models.PavlovDaysOfWeek

data class GoalsState(
    val pendingGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    var showPopup: Boolean = false,
    var isEditMode: Boolean = false,
    var newGoal: Goal = Goal(),
    var showTimePickerDialog: Boolean = false
)