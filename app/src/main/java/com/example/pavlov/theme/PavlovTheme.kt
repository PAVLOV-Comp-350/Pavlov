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
private val DarkPurple = Color(0xFF3F2C70)
private val DeepPurple = Color(0xFF2A1B50)
private val LightPurple = Color(0xFF9C8BC1)
private val TrueBlack = Color(0xFF000000) // totally black background
private val DarkGrey = Color(0xFF121212) // almost black
private val MediumGrey = Color(0xFF1A1A1A) // dark grey
private val LightGrey = Color(0xFF252525) // medium grey
private val OffWhite = Color(0xFFF0F0F0)
private val AccentColor = Color(0xFF8E77C5)

// Light theme colors
private val LightThemePurple = Color(0xFF7C4DFF)
private val LightThemeBackground = Color(0xFFF5F5F5) // light grey (not white)
private val LightThemeSurface = Color(0xFFFFFFFF) // white

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = DarkPurple,
    primaryContainer = DeepPurple,
    secondary = LightPurple,
    background = TrueBlack,
    surface = DarkGrey,
    onPrimary = OffWhite,
    onSecondary = DarkGrey,
    onBackground = OffWhite,
    onSurface = OffWhite,
    surfaceVariant = MediumGrey,
    onSurfaceVariant = Color(0xFFD0D0D0),
    error = Color(0xFFF44336)
)

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = LightThemePurple,
    primaryContainer = DarkPurple,
    secondary = AccentColor,
    background = LightThemeBackground,
    surface = LightThemeSurface,
    onPrimary = OffWhite,
    onSecondary = OffWhite,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE4E4E4),
    onSurfaceVariant = Color(0xFF444444)
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