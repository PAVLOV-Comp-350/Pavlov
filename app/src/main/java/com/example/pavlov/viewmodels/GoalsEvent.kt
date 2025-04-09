package com.example.pavlov.viewmodels

import com.example.pavlov.models.PavlovDayOfWeek
import com.example.pavlov.utils.Vec2

/* *
*  This interface defines all of the user interaction events that happen in the Goals Screen
*  */
sealed interface GoalsEvent : AnyEvent {
    data class MarkGoalComplete(val goalId: Int) : GoalsEvent
    data object ShowAddGoalAlert : GoalsEvent
    data object HideAddGoalAlert : GoalsEvent
    data class SetGoalTitle(val title: String) : GoalsEvent
    data class SetGoalDescription(val description: String) : GoalsEvent
    data class SetGoalStreak(val streak: Int): GoalsEvent
    data class ToggleGoalDay(val day: PavlovDayOfWeek): GoalsEvent
    data object ConfirmAddGoal : GoalsEvent
    data class ShowEditGoalAlert(val goalId: Int) : GoalsEvent
    data class DeleteGoal(val goalId: Int) : GoalsEvent
    data object ShowTimePicker : GoalsEvent
    data object HideTimePicker : GoalsEvent
    data class SetScheduledTime(val minutes: Int) : GoalsEvent
}