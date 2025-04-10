package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.room.util.getColumnIndex
import com.example.pavlov.PavlovApplication
import com.example.pavlov.models.RouletteGameState
import com.example.pavlov.models.ScratcherCell
import com.example.pavlov.models.ScratcherGameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
                val roulettePicks = listOf("1 - 12", "13 - 24", "25 - 36", "00") + (0..36).map { it.toString() }

                // Safely access rouletteGameState
                _state.value.rouletteGameState?.let { gameState ->
                    val myPick = roulettePicks.getOrNull(gameState.pickIndex) ?: "Invalid Pick"

                    _state.update {
                        it.copy(
                            rouletteGameState = gameState.copy(
                                pick = myPick
                            )
                        )
                    }
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
                }
            }

            is RouletteEvent.StopSpinning -> {
                _state.value.rouletteGameState?.let { gameState ->
                    if(gameState.win == gameState.pick) {
                        if(gameState.pickIndex <= 3){
                            _state.update {
                                it.copy(
                                    rouletteGameState = gameState.copy(
                                        isSpinning = false,
                                        totalPrize = 16
                                    )
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    rouletteGameState = gameState.copy(
                                        isSpinning = false,
                                        totalPrize = 280
                                    )
                                )
                            }
                        }
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
        val picks = listOf("00") + (0..36).map {it.toString()}
        val newWin = picks.random()
        return newWin
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