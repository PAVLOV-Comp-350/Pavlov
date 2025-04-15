package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.room.util.getColumnIndex
import com.example.pavlov.PavlovApplication
import com.example.pavlov.models.RouletteGameState
import com.example.pavlov.models.ScratcherCell
import com.example.pavlov.models.ScratcherGameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class CasinoViewModel: ViewModel() {
    // The internal state of the view model is private so that the UI can only
    // send updates to the state through the GoalsEvent Interface
    private val _state = MutableStateFlow(CasinoState())
    // Consumers of the GoalViewModel API subscribe to this StateFlow
    // to receive update to the UI state
    val state = _state.asStateFlow()

    fun onEvent(event: CasinoEvent) {
        when (event) {
            is CasinoEvent.SelectGame -> {
                _state.update { it.copy(selectedGame = event.game) }
            }

            is CasinoEvent.CloseGameDialog -> {
                _state.update {
                    it.copy(
                        selectedGame = null,
                        scratcherGameState = null,
                        rouletteGameState = null
                    )
                }
            }

            is CasinoEvent.PlayGame -> {
                when (event.game.name) {
                    "Scratcher" -> initScratcherGame()
                    "Roulette" -> initRouletteGame()
                    else -> {}
                }
            }

            is CasinoEvent.SpendTreats -> {
                PavlovApplication.removeTreats(event.amount)
            }

            is CasinoEvent.ScratcherEvent -> {
                handleScratcherEvent(event.event)
            }

            is CasinoEvent.RouletteEvent -> {
                handleRouletteEvent(event.event)
            }
        }
    }

    private fun handleRouletteEvent(event: RouletteEvent) {
        when(event) {
            is RouletteEvent.StartNewGame -> {
                initRouletteGame()
            }

            is RouletteEvent.SelectPick -> {
                val gameState = _state.value.rouletteGameState ?: return

                if (gameState.isSpinning) return

                val roulettePicks = gameState.board
                val selectedPick = roulettePicks.getOrNull(event.pickIndex) ?: "Invalid Pick"

                _state.update {
                    it.copy(
                        rouletteGameState = gameState.copy(
                            pickIndex = event.pickIndex,
                            pick = selectedPick
                        )
                    )
                }
            }


            is RouletteEvent.Spin -> {
                _state.value.rouletteGameState?.let { gameState ->
                    _state.update {
                        it.copy(
                            rouletteGameState = gameState.copy(
                                isSpinning = true
                            )
                        )
                    }

                    // Launch the coroutine to stop spinning after 3 seconds
                    viewModelScope.launch {
                        delay(3000L) // Wait 3 seconds

                        // Randomly select the winning pick
                        val randomWin = rouletteGetRandomWin()

                        _state.update {
                            it.copy(
                                rouletteGameState = it.rouletteGameState?.copy(
                                    win = randomWin
                                )
                            )
                        }

                        // Trigger StopSpinning
                        handleRouletteEvent(RouletteEvent.StopSpinning)
                    }
                }
            }

            is RouletteEvent.StopSpinning -> {
                _state.value.rouletteGameState?.let { gameState ->
                    val winningNumber = gameState.win
                    val selectedIndex = gameState.pickIndex
                    val selectedPick = gameState.pick

                    val isWin = when (selectedPick) {
                        "1 - 12" -> {
                            winningNumber.toIntOrNull()?.let { it in 1..12 } ?: false
                        }

                        "13 - 24" -> {
                            winningNumber.toIntOrNull()?.let { it in 13..24 } ?: false
                        }

                        "25 - 36" -> {
                            winningNumber.toIntOrNull()?.let { it in 25..36 } ?: false
                        }

                        else -> {
                            // Exact match for "00", "0", or number strings
                            selectedPick == winningNumber
                        }
                    }
                    val prize = if (isWin) {
                        if (selectedPick == "00" || selectedPick == "0") {
                            280
                        } else if (selectedIndex <= 3) { // These are group bets: 1-12, 13-24, 25-36
                            16
                        } else {
                            280
                        }
                    } else 0

                    val displayMessage = if (isWin) "You Win!" else "You Lose!"
                    val message = "Winning Number: $winningNumber $displayMessage"

                    _state.update {
                        it.copy(
                            rouletteGameState = gameState.copy(
                                totalPrize = prize,
                                resultMessage = message
                            )
                        )
                    }

                    //Auto-close game
                    viewModelScope.launch {
                        delay(3000)
                        handleRouletteEvent(RouletteEvent.CollectPrize)
                        handleRouletteEvent(RouletteEvent.CloseGame)
                    }
                }
            }

            is RouletteEvent.CollectPrize -> {
                _state.value.rouletteGameState?.let { gameState ->
                    if (gameState.totalPrize > 0) {
                        PavlovApplication.addTreats(gameState.totalPrize)
                        _state.update {
                            it.copy(
                                rouletteGameState = null,
                                selectedGame = null
                            )
                        }
                    }
                }
            }

            is RouletteEvent.CloseGame -> {
                _state.update {
                    it.copy(
                        rouletteGameState = null,
                        selectedGame = null
                    )
                }
            }
        }
    }

    private fun initRouletteGame() {
        val win = rouletteGetRandomWin()

        _state.update {
            it.copy(
                rouletteGameState = RouletteGameState(
                    isSpinning = false,
                    pick = "",
                    board = listOf("1 - 12", "13 - 24", "25 - 36", "00") + (0..36).map {it.toString()},
                    pickIndex = 0,
                    win = win,
                    totalPrize = 0
                )
            )
        }
    }

    private fun rouletteGetRandomWin(): String{
        val picks = listOf("00", "0") + (1..36).map {it.toString()}
        return picks.random()
    }

    private fun handleScratcherEvent(event: ScratcherEvent) {
        when (event) {
            is ScratcherEvent.StartNewGame -> {
                initScratcherGame()
            }

            is ScratcherEvent.ScratchCell -> {
                _state.value.scratcherGameState?.let { gameState ->
                    if (gameState.isComplete) return

                    val cells = gameState.cells.toMutableList()
                    if (event.index < cells.size && !cells[event.index].isRevealed) {

                        cells[event.index] = cells[event.index].copy(isRevealed = true)

                        val isComplete = cells.all { it.isRevealed }

                        val totalPrize = cells.filter { it.isRevealed }.sumOf { it.value }

                        _state.update {
                            it.copy(
                                scratcherGameState = gameState.copy(
                                    cells = cells,
                                    isComplete = isComplete,
                                    totalPrize = totalPrize
                                )
                            )
                        }

                        if (isComplete) {
                            handleScratcherCompletion(totalPrize)
                        }
                    }
                }
            }

            is ScratcherEvent.CollectPrize -> {
                _state.value.scratcherGameState?.let { gameState ->
                    if (gameState.isComplete && gameState.totalPrize > 0) {
                        PavlovApplication.addTreats(gameState.totalPrize)
                        _state.update {
                            it.copy(
                                scratcherGameState = null,
                                selectedGame = null
                            )
                        }
                    }
                }
            }

            is ScratcherEvent.CloseGame -> {
                _state.update {
                    it.copy(
                        scratcherGameState = null,
                        selectedGame = null
                    )
                }
            }
        }
    }

    private fun initScratcherGame() {
        val cellValues = generateScratcherValues()
        val cells = cellValues.map { ScratcherCell(value = it, isRevealed = false) }

        _state.update {
            it.copy(
                scratcherGameState = ScratcherGameState(
                    cells = cells,
                    isComplete = false,
                    totalPrize = 0
                )
            )
        }
    }

    private fun generateScratcherValues(): List<Int> {
        val possibleValues = listOf(0, 1, 2, 5, 10, 20, 100)
        val weights = listOf(75, 10, 5, 2, 1, 1, 1) // out of 100

        val totalWeight = weights.sum()
        val normalizedWeights = weights.map { it.toDouble() / totalWeight }

        return List(9) {
            val rnd = Random.nextDouble()
            var cumulativeProbability = 0.0

            var selectedValue = possibleValues[0]
            for (i in possibleValues.indices) {
                cumulativeProbability += normalizedWeights[i]
                if (rnd <= cumulativeProbability) {
                    selectedValue = possibleValues[i]
                    break
                }
            }
            selectedValue
        }
    }

    private fun handleScratcherCompletion(prizeAmount: Int) {
        Log.d("CasinoViewModel", "Scratcher game completed with prize: $prizeAmount")

    }
}