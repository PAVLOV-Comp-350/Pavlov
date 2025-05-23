package com.example.pavlov.viewmodels

sealed interface RouletteEvent {
    data object StartNewGame: RouletteEvent
    data object RestartGame: RouletteEvent
    data object CloseGame: RouletteEvent
    data object Spin: RouletteEvent
    data object StopSpinning: RouletteEvent
    data class SelectPick(val pickIndex: Int): RouletteEvent
}