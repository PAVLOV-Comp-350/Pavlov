package com.example.pavlov.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.pavlov.PavlovApplication

/**
 * dark mode toggle switch
 */
@Composable
fun ThemeSwitch(
    modifier: Modifier = Modifier
) {
    val isDarkMode by PavlovApplication.isDarkTheme.collectAsState()
    Switch(
        checked = isDarkMode,
        onCheckedChange = { value ->
            PavlovApplication.setDarkTheme(value)
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