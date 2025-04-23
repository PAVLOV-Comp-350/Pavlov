package com.example.pavlov.viewmodels

import com.example.pavlov.models.CardGameState
import com.example.pavlov.models.CasinoGame
import com.example.pavlov.models.RouletteGameState
import com.example.pavlov.models.ScratcherGameState
import com.example.pavlov.models.SlotsGameState

data class CasinoState(
    val selectedGame: CasinoGame? = null,
    val isLoading: Boolean = false,
    val scratcherGameState: ScratcherGameState? = null,
    val rouletteGameState: RouletteGameState? = null,
    val cardGameState: CardGameState? = null,
    val slotsGameState: SlotsGameState? = null

)
