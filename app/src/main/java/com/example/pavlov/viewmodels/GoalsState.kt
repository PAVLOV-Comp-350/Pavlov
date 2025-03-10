package com.example.pavlov.viewmodels

import com.example.pavlov.models.Goal

data class GoalsState(
    val goals: List<Goal> = emptyList(),
    // TODO: Team needs to have a discussion about how we are going to track the completion
    //  of goals. For now we will just mock this information for demo purposes.
    val completedGoals: Map<Int, Boolean> = emptyMap(),
    val treats: Int = 0,
)
