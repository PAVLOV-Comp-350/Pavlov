package com.example.pavlov.viewmodels

import com.example.pavlov.models.CasinoGame

sealed interface CasinoEvent : AnyEvent {
    data class SelectGame(val game: CasinoGame) : CasinoEvent
    data object CloseGameDialog : CasinoEvent
    data class PlayGame(val game: CasinoGame) : CasinoEvent
    data class SpendTreats(val amount: Int) : CasinoEvent
    data class ScratcherEvent(val event: com.example.pavlov.viewmodels.ScratcherEvent) : CasinoEvent
    data class RouletteEvent( val event: com.example.pavlov.viewmodels.RouletteEvent) : CasinoEvent
    data class CardEvent( val event: com.example.pavlov.viewmodels.CardEvent) : CasinoEvent
    data class SlotsEvent(val event: com.example.pavlov.viewmodels.SlotsEvent) : CasinoEvent

}