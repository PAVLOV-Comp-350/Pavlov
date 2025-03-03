package com.example.pavlov

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * dark mode toggle switch
 */
@Composable
fun ThemeSwitch(
    modifier: Modifier = Modifier
) {
    Switch(
        checked = ThemeManager.isDarkTheme,
        onCheckedChange = { checked ->
            ThemeManager.isDarkTheme = checked
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
    )
}