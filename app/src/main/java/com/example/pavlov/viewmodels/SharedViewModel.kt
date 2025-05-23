package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pavlov.PavlovApplication
import com.example.pavlov.utils.EPSILON
import com.example.pavlov.utils.Vec2
import com.example.pavlov.utils.getRandomUnitVec2
import com.example.pavlov.utils.minus
import com.example.pavlov.utils.plus
import com.example.pavlov.utils.times
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.random.Random
//import com.example.pavlov.utils.getRank
import com.example.pavlov.utils.getCurrentRankStartXp
import com.example.pavlov.utils.getNextRankXP
import com.example.pavlov.utils.getRank


class SharedViewModel: ViewModel() {
    // The internal state of the view model is private so that the UI can only
    // send updates to the state through the GoalsEvent Interface
    private val _state = MutableStateFlow(SharedState())
    // Consumers of the GoalViewModel API subscribe to this StateFlow
    // to receive update to the UI state
    val state = combine(_state, PavlovApplication.treats, PavlovApplication.xp, PavlovApplication.maxXp) { shared, treats, xp, maxXp ->
        shared.copy(
            treats = treats,
            currentXp = xp,
            maxXp = maxXp
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)


    fun onEvent(event: SharedEvent) {
        when(event) {
            is SharedEvent.SetScreen -> {
                _state.value = _state.value.copy(
                    activeScreen = event.screen
                )
            }
            is SharedEvent.UpdateRewardCollectables -> updateAllCollectables(_state.value.collectableTarget)
            is SharedEvent.GenerateCollectableRewards -> {
                generateRewards(event.origin)
            }
            is SharedEvent.SetCollectablesTarget -> {
                _state.value = _state.value.copy(
                    collectableTarget = event.target
                )
            }
            is SharedEvent.UpdateXp -> {
                _state.update { current ->
                    val oldTitle = getRank(current.currentXp)
                    val newTitle = getRank(event.newXp)

                    current.copy(
                        currentXp = event.newXp,
                        maxXp = event.newMaxXp,
                        manualTitle = if(newTitle > oldTitle) null else current.manualTitle
                    )
                }
            }
            is SharedEvent.GainXpFromTask -> {
                gainXpFromTask()
            }

            is SharedEvent.ManualTitle -> {
                _state.update { current ->
                    current.copy(
                        manualTitle = event.title
                    )
                }
            }
        }
    }

    private fun updateAllCollectables(target: Vec2) {
        _state.update { current ->
            val updatedRewards = mutableListOf<GoalRewardCollectable>()
            current.rewardCollectables.forEach {
                // Apply the impulse
                val desired = (target - it.pos).normalize0().scale(40f)
                val steering = (desired - it.vel)

                // FIXME: Use delta time to make this frame-rate independent
                val newVel = it.vel * it.dampingCoefficient + steering
                val newPos = it.pos + it.vel
                // TODO: Add a particle age in case of stragglers
                if (newPos.dist(target) > 15f) {
                    updatedRewards += it.copy(pos = newPos, vel = newVel)
                } else {
                    PavlovApplication.addTreats(it.value)
                }
            }
            current.copy(rewardCollectables = updatedRewards)
        }
    }

    /**
     * @param origin The position where the rewards will be placed
     */
    private fun generateRewards(origin: Vec2) {
        // TODO: Scale according to task streak progress and user abilities/level
        val numDroppedCollectables = Random.nextInt(3, 10)
        val damping = 0.86f
        val launchPower = 100f
        val newCollectables = List(numDroppedCollectables) {
            GoalRewardCollectable(
                pos = origin,
                vel = getRandomUnitVec2() * launchPower,
                value = Random.nextInt(1, 4),
                alpha = 1f,
                dampingCoefficient = 0.86f,
            )
        }
        _state.update { current ->
            current.copy(
                rewardCollectables = current.rewardCollectables.plus(newCollectables)
            )
        }
    }

    fun gainXpFromTask() {
        val newXp = PavlovApplication.xp.value + 80
        val nextMaxXp = getNextRankXP(newXp)

        PavlovApplication.setXp(newXp)
        PavlovApplication.setMaxXp(nextMaxXp)

        _state.update {
            it.copy(currentXp = newXp, maxXp = nextMaxXp)
        }
    }
}