package com.example.pavlov.models

/**
 * state of the slots game
 */
data class SlotsGameState(
    val reels: List<List<String>> = List(3) { generateReelSymbols() },
    val spinningReels: List<Boolean> = List(3) { false },
    val isSpinning: Boolean = false,
    val stopPosition: List<Int> = List(3) { 0 },
    val totalPrize: Int = 0,
    val resultMessage: String = "",
    val spinCost: Int = 3,
    val freeFirstSpin: Boolean = true
)

/**
 * Generate symbols for a slot machine reel
 */
fun generateReelSymbols(): List<String> {
    return listOf(
        "🍒",
        "🍋",
        "🍊",
        "🍇",
        "🔔",
        "💎",
        "7️⃣"
    )
}

/**
 * Symbol payout values
 */
val symbolValues = mapOf(
    "🍒" to 2,
    "🍋" to 3,
    "🍊" to 5,
    "🍇" to 10,
    "🔔" to 15,
    "💎" to 20,
    "7️⃣" to 50
)