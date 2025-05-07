package com.example.pavlov.models

data class RouletteGameState(
    val isSpinning: Boolean = false,
    val pick: String = "",
    val board: List<String> = listOf("Red", "Black", "1 - 12", "13 - 24", "25 - 36", "00") + (0..36).map {it.toString()},
    val pickIndex: Int = -1,
    val win: String = "",
    val totalPrize: Int = 0,
    val resultMessage: String = ""
)