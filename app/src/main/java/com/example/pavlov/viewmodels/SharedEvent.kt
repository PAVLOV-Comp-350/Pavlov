package com.example.pavlov.viewmodels

import com.example.pavlov.utils.Vec2
import com.example.pavlov.views.Destination

sealed interface SharedEvent : AnyEvent {
    data class Navigate(val destination: Destination) : SharedEvent
    data class SetCollectablesTarget(val target: Vec2) : SharedEvent
    data object UpdateRewardCollectables : SharedEvent
    data class GenerateCollectableRewards(val origin: Vec2) : SharedEvent
    data class UpdateXp(val newXp: Int, val newMaxXp: Int): SharedEvent
    data object GainXpFromTask : SharedEvent
}