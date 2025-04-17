package com.example.pavlov.viewmodels

sealed interface CardEvent {
    data object StartNewGame: CardEvent
    data object CloseGame: CardEvent
    data object CollectPrize: CardEvent
}