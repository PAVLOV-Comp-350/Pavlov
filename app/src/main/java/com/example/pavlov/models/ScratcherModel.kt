package com.example.pavlov.models


/**
 * state of the scratcher game
 */
data class ScratcherGameState(
    val cells: List<ScratcherCell> = List(9) { ScratcherCell() },
    val isComplete: Boolean = false,
    val totalPrize: Int = 0
)

/**
 * a single cell in the scratcher game
 */
data class ScratcherCell(
    val value: Int = 0,
    val isRevealed: Boolean = false
)