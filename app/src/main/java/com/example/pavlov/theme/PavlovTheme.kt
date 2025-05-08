package com.example.pavlov.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.pavlov.PavlovApplication

// Dark theme colors
private val Burgundy = Color(0xFF800020)
private val DeepBurgundy = Color(0xFF590016)
private val TrueBlack = Color(0xFF000000)
private val DarkGrey = Color(0xFF121212)
private val LightGrey = Color(0xFF757575)
private val OffWhite = Color(0xFFF0F0F0)
private val AccentColor = Color(0xFFDAA520)
private val tertiaryGreen = Color(0xFF00796B)
private val ErrorRed = Color(0xFFF44336)

// Light theme colors
private val RoyalBlue = Color(0xFF4169E1)
private val LightSilver = Color(0xFFE8E8E8)
private val Silver = Color(0xFFC0C0C0)
private val LightTeal = Color(0xFF546E7A)

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = Burgundy,
    primaryContainer = DeepBurgundy,
    secondary = AccentColor,
    background = TrueBlack,
    surface = DarkGrey,
    onPrimary = OffWhite,
    onSecondary = DarkGrey,
    onBackground = OffWhite,
    onSurface = OffWhite,
    surfaceVariant = LightGrey,
    onSurfaceVariant = OffWhite,
    tertiary = tertiaryGreen,
    onTertiary = OffWhite,
    error = ErrorRed,
    onError = OffWhite,
)

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    primaryContainer = Silver,
    secondary = AccentColor,
    background = LightSilver,
    surface = Color.White,
    onPrimary = OffWhite,
    onSecondary = DarkGrey,
    onBackground = DarkGrey,
    onSurface = DarkGrey,
    surfaceVariant = Silver,
    onSurfaceVariant = DarkGrey,
    tertiary = LightTeal,
    onTertiary = Color.White,
    error = ErrorRed,
    onError = OffWhite
)

@Composable
fun PavlovTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}