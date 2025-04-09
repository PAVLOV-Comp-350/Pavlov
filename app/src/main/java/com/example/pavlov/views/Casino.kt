package com.example.pavlov.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pavlov.viewmodels.AnyEvent
import com.example.pavlov.viewmodels.CasinoEvent
import com.example.pavlov.viewmodels.CasinoState
import com.example.pavlov.viewmodels.SettingsEvent
import com.example.pavlov.viewmodels.SharedState

/**
 * Casino Screen
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasinoScreen(
    state: CasinoState,
    sharedState: SharedState,
    onEvent: (AnyEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
){
    Scaffold(
        topBar = { PavlovTopBar(sharedState, onEvent = {onEvent(it)}) },
        bottomBar = { PavlovNavbar(activeScreen = sharedState.activeScreen, onNavigate = onNavigate) },
    ){ paddingValues ->
        Box(Modifier.padding(paddingValues)){
            Text(
                text = "Casino Games to be done here"
            )
        }

    }
}