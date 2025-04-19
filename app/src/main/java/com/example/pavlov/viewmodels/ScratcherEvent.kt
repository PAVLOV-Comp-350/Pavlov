package com.example.pavlov.viewmodels

sealed interface ScratcherEvent {
    data object StartNewGame : ScratcherEvent
    data object RestartGame : ScratcherEvent
    data class ScratchCell(val index: Int) : ScratcherEvent
    data object CloseGame : ScratcherEvent
}