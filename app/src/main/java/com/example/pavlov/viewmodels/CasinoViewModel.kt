package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.room.util.getColumnIndex
import com.example.pavlov.PavlovApplication
import com.example.pavlov.models.CardGameState
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
                        rouletteGameState = null,
                        cardGameState = null
                    )
                }
            }

            is CasinoEvent.PlayGame -> {
                when (event.game.name) {
                    "Scratcher" -> initScratcherGame()
                    "Roulette" -> initRouletteGame()
                    "Cards" -> initCardGame()
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

            is CasinoEvent.CardEvent -> {
                handleCardEvent(event.event)
            }
        }
    }

    private fun initCardGame(){
        _state.update {
            it.copy (
                cardGameState = CardGameState(

                )
            )
        }
    }

    private fun handleCardEvent(event: CardEvent){
        when(event) {
            is CardEvent.StartNewGame -> {
                initCardGame()
            }

            is CardEvent.CollectPrize -> {
                _state.value.cardGameState?.let { gameState ->
                    if (gameState.totalPrize > 0) {
                        PavlovApplication.addTreats(gameState.totalPrize)
                        _state.update {
                            it.copy(
                                cardGameState = null,
                                selectedGame = null
                            )
                        }
                    }
                }
            }

            is CardEvent.CloseGame -> {
                _state.update {
                    it.copy(
                        cardGameState = null,
                        selectedGame = null
                    )
                }
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
                    //only spins roulette if a bet is picked
                    if(gameState.pick.isBlank()){
                        return@let
                    }

                    if (!gameState.isSpinning) {
                        _state.update {
                            it.copy(
                                rouletteGameState = gameState.copy(
                                    isSpinning = true
                                )
                            )
                        }

                        viewModelScope.launch {
                            delay(3000L) // Wait 3 seconds

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
            }

            is RouletteEvent.StopSpinning -> {
                _state.value.rouletteGameState?.let { gameState ->
                    val winningNumberStr = gameState.win.trim()         // e.g. "17" or "00"
                    val selectedPickStr = gameState.pick.trim()         // e.g. "1 - 12" or "17"

                    val winningNumberInt = winningNumberStr.toIntOrNull()

                    val isWin = if (selectedPickStr.contains("-")) {
                        // Handle range-based picks like "1 - 12"
                        val rangeParts = selectedPickStr.split("-").map { it.trim().toIntOrNull() }
                        if (rangeParts.size == 2 && winningNumberInt != null) {
                            val (start, end) = rangeParts
                            if (start != null && end != null) {
                                winningNumberInt in start..end
                            } else false
                        } else false
                    } else {
                        // Exact match case (numbers or "0", "00")
                        selectedPickStr == winningNumberStr
                    }

                    val prize = if (isWin) {
                        when {
                            selectedPickStr == "0" || selectedPickStr == "00" -> 280
                            selectedPickStr.contains("-") -> 16
                            else -> 280
                        }
                    } else 0

                    val displayMessage = if (isWin) "You Win!" else "You Lose!"
                    val message = "Winning Number: $winningNumberStr $displayMessage"

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
        _state.update {
            it.copy(
                rouletteGameState = RouletteGameState(
                    isSpinning = false,
                    pick = "",
                    board = listOf("1 - 12", "13 - 24", "25 - 36", "00") + (0..36).map {it.toString()},
                    pickIndex = -1,
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