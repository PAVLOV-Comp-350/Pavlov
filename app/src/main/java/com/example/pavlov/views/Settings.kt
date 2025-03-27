package com.example.pavlov.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pavlov.PavlovApplication
import com.example.pavlov.theme.ThemeSwitch
import com.example.pavlov.viewmodels.SettingsEvent
import com.example.pavlov.viewmodels.SettingsState
import com.example.pavlov.viewmodels.SharedState

/**
 * Main screen for showing all the goals
 *
 * @param state Immutable state passed in by the GoalsViewModel
 * @param onEvent Main callback for processing user interaction events
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    sharedState: SharedState,
    onEvent: (SettingsEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    Scaffold(
        topBar = { PavlovTopBar(sharedState) },
        bottomBar = { PavlovNavbar(activeScreen = sharedState.activeScreen, onNavigate = onNavigate) },
    ) { padding ->
        val isDarkMode by PavlovApplication.isDarkTheme.collectAsState()
        Column(modifier = Modifier.padding(padding)) {
            SettingsToggleRow(
                title = "Dark Theme",
                checked = isDarkMode,
                onCheckedChanged = { PavlovApplication.setDarkTheme(it) }
            )
            // Add other setting rows here...
        }
    }

}

@Composable
fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChanged
        )
    }
}