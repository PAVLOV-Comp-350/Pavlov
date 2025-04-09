package com.example.pavlov.viewmodels

sealed interface ScratcherEvent {
    data object StartNewGame : ScratcherEvent
    data class ScratchCell(val index: Int) : ScratcherEvent
    data object CollectPrize : ScratcherEvent
    data object CloseGame : ScratcherEvent
}