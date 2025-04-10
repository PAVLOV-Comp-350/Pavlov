package com.example.pavlov.viewmodels

import com.example.pavlov.utils.Vec2
import com.example.pavlov.views.Screen

data class SharedState(
    val activeScreen: Screen = Screen.Goals,
    val treats: Int = 0,
    val rewardCollectables: List<GoalRewardCollectable> = emptyList(),
    val collectableTarget: Vec2 = Vec2.Zero,
    val currentXp: Int = 0,
    val maxXp: Int = 100
)
