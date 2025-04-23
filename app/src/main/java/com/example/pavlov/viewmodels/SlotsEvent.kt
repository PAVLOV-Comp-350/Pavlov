package com.example.pavlov.viewmodels

sealed interface SlotsEvent {
    data object StartNewGame: SlotsEvent
    data object RestartGame: SlotsEvent
    data object CloseGame: SlotsEvent
    data object Spin: SlotsEvent
    data object StopSpinning: SlotsEvent
}