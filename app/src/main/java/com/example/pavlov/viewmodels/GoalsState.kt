package com.example.pavlov.viewmodels

import com.example.pavlov.models.Goal
import com.example.pavlov.models.GoalFrequency

data class GoalsState(
    val goals: List<Goal> = emptyList(),
    // TODO: Team needs to have a discussion about how we are going to track the completion
    //  of goals. For now we will just mock this information for demo purposes.
    val completedGoals: Map<Int, Boolean> = emptyMap(),
    val treats: Int = 0,
    var showPopup: Boolean = false,
    var isEditMode: Boolean = false,
    var newGoalId: Int = 0,
    var newGoalTitle: String = "",
    var newGoalDescription: String = "",
    var newGoalStreak: Int = 0,
    var newGoalFrequency: GoalFrequency = GoalFrequency.DAILY,
    var newGoalSimple: Boolean = false,
    var newGoalUnit: String = "No unit",
    var newGoalCurrent: Int = 0,
    var newGoalTarget: Int = 0,
    var newGoalActiveDays: Int = 127,
    var newGoalScheduledTimeMinutes: Int = 540,
    var showTimePickerDialog: Boolean = false
)
