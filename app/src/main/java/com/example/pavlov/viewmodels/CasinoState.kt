package com.example.pavlov.viewmodels

import com.example.pavlov.models.CasinoGame
import com.example.pavlov.models.ScratcherGameState

data class CasinoState(
    val selectedGame: CasinoGame? = null,
    val isLoading: Boolean = false,
    val scratcherGameState: ScratcherGameState? = null
)
