package com.example.pavlov.viewmodels

import com.example.pavlov.utils.Vec2
import com.example.pavlov.views.Screen

sealed interface SharedEvent : AnyEvent {
    data class SetScreen(val screen: Screen) : SharedEvent
    data class SetCollectablesTarget(val target: Vec2) : SharedEvent
    data object UpdateRewardCollectables : SharedEvent
    data class GenerateCollectableRewards(val origin: Vec2) : SharedEvent
    data class UpdateXp(val newXp: Int, val newMaxXp: Int): SharedEvent
    data object GainXpFromTask : SharedEvent
    data class ManualTitle(val title:String?) : SharedEvent
}