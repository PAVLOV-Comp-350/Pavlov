package com.example.pavlov.viewmodels

sealed interface CardEvent {
    data object StartNewGame: CardEvent
    data object RestartGame: CardEvent
    data object CloseGame: CardEvent
    data object CollectPrize: CardEvent
    data object DealFromDeck: CardEvent
    data object Redraw: CardEvent
    data class HoldCard(val card: String) : CardEvent
    data object CheckHand: CardEvent
}