package com.example.pavlov.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Themed colors for the Casino section of the app
 */
object CasinoTheme {
    // Game gradient
    val GoldGradient = listOf(
        Color(0xFFBF953F),
        Color(0xFFDFC881),
        Color(0xFFBF953F)
    )

    val SilverGradient = listOf(
        Color(0xFFC0C0C0),
        Color(0xFFE8E8E8),
        Color(0xFFA8A8A8)
    )
    val BronzeGradient = listOf(
        Color(0xFFCD7F32),
        Color(0xFFE6B77D),
        Color(0xFF915C15))
    val PlatinumGradient = listOf(
        Color(0xFF8F9190),
        Color(0xFFE5E6E6),
        Color(0xFF797A7A)
    )
    val EmeraldGradient = listOf(
        Color(0xFF046307),
        Color(0xFF0A8F0D),
        Color(0xFF054D08)
    )

    val PlayButtonColor = Color(0xFF2E7D32)
    val CopperAccent = Color(0xFFA84315)
    val CoralAccent = Color(0xFF607D8B)

    @Composable
    fun getTreatIndicatorColor(): Color {
        return if (MaterialTheme.colorScheme.isLight()) {
            CoralAccent
        } else {
            CopperAccent
        }
    }
}

fun androidx.compose.material3.ColorScheme.isLight(): Boolean {
    val backgroundLuminance = background.luminance()
    return backgroundLuminance > 0.5f
}

fun Color.luminance(): Float {
    val r = red * 0.299f
    val g = green * 0.587f
    val b = blue * 0.114f
    return r + g + b
}


