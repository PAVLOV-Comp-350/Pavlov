package com.example.pavlov.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data class representing a casino with styling options
 */
data class CasinoGame(
    val name: String,
    val iconResId: Int? = null,
    val iconVector: ImageVector? = null,
    val description: String = "",
    val gradient: List<Color>,
    val costInTreats: Int? = null,
    val iconTint: Color? = null,
    val iconSize: Dp = 124.dp      // icon size
) {
    init {
        require(iconResId != null || iconVector != null) { "Either iconResId or iconVector must be provided"}
    }
}