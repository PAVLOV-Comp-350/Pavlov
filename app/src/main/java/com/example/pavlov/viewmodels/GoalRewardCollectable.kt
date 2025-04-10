package com.example.pavlov.viewmodels

import com.example.pavlov.utils.Vec2

data class GoalRewardCollectable(
    val value: Int,
    val alpha: Float = 1f,
    val pos: Vec2,
    val vel: Vec2,
    val dampingCoefficient: Float,
)