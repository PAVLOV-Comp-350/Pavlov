package com.example.pavlov.viewmodels

import com.example.pavlov.models.CasinoGame

data class CasinoState(
    val selectedGame: CasinoGame? = null,
    val showGameDialog: Boolean = false,
    val gameResults: Map<String, Int> = emptyMap(),
    val isPlaying: Boolean = false
)
