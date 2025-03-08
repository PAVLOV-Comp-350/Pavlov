package com.example.pavlov.viewmodels

/* *
*  This interface defines all of the user interaction events that happen in the Goals Screen
*  */
sealed interface GoalsEvent {
    // TODO: This should actually be broken into a few UI interactions.
    //  Something like ShowAddGoalDialog, HideAddGoalDialog, and SaveNewGoal
    data object ShowAddGoalAlert : GoalsEvent
    data object HideAddGoalAlert : GoalsEvent
    data class MarkGoalComplete(val goalId: Int) : GoalsEvent
    data class ConfirmAddGoal(val id: Int, val title: String, val description: String, val streak: Int) : GoalsEvent
}