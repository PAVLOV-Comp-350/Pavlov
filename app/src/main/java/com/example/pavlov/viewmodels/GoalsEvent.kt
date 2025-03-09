package com.example.pavlov.viewmodels

/* *
*  This interface defines all of the user interaction events that happen in the Goals Screen
*  */
sealed interface GoalsEvent {
    // TODO: This should actually be broken into a few UI interactions.
    //  Something like ShowAddGoalDialog, HideAddGoalDialog, and SaveNewGoal
    data object ShowAddGoalAlert : GoalsEvent
    data object HideAddGoalAlert : GoalsEvent
    data class SetGoalId(val id: Int) : GoalsEvent
    data class SetGoalTitle(val title: String) : GoalsEvent
    data class SetGoalDescription(val description: String) : GoalsEvent
    data class SetGoalStreak(val streak: Int): GoalsEvent
    data class MarkGoalComplete(val goalId: Int) : GoalsEvent
    data object ConfirmAddGoal : GoalsEvent
}