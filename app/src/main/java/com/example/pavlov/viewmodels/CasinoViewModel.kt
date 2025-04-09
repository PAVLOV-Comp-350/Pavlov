package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.pavlov.PavlovApplication
import com.example.pavlov.models.Scratcher
import com.example.pavlov.models.ScratcherCell
import com.example.pavlov.models.ScratcherGameState
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

    private val scratcherDao = PavlovApplication.local_db.scratcherDao()


    fun onEvent(event: CasinoEvent) {
        when (event) {
            is CasinoEvent.SelectGame -> {
                _state.update { it.copy(selectedGame = event.game) }
            }

            is CasinoEvent.CloseGameDialog -> {
                _state.update {
                    it.copy(
                        selectedGame = null,
                        scratcherGameState = null
                    )
                }
            }

            is CasinoEvent.PlayGame -> {
                when (event.game.name) {
                    "Scratcher" -> initScratcherGame()
                    else -> {}
                }
            }

            is CasinoEvent.SpendTreats -> {
                // Spend treats to play games
                PavlovApplication.removeTreats(event.amount)
            }

            is CasinoEvent.ScratcherEvent -> {
                handleScratcherEvent(event.event)
            }
        }
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
                        // Reveal the cell
                        cells[event.index] = cells[event.index].copy(isRevealed = true)

                        // Check if game is complete (all cells revealed)
                        val isComplete = cells.all { it.isRevealed }

                        // Calculate total prize from revealed cells
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

                        // If game is complete, save the result
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
        // Generate random values for each cell
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
        // possible prize values and their probabilities
        val possibleValues = listOf(0, 1, 2, 5, 10, 20, 100)
        val weights = listOf(75, 10, 5, 2, 1, 1, 1) // out of 100

        val totalWeight = weights.sum()
        val normalizedWeights = weights.map { it.toDouble() / totalWeight }

        // Generate 9 random values based on probability weights
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

        viewModelScope.launch {
            val scratcher = Scratcher(
                isScratched = true,
                prizeAmount = prizeAmount
            )
            scratcherDao.insert(scratcher)
        }
    }
}