package com.example.pavlov.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pavlov.viewmodels.AnyEvent
import com.example.pavlov.viewmodels.PetState
import com.example.pavlov.viewmodels.SharedState

@Composable
fun PetScreen(
state: PetState,
sharedState: SharedState,
onEvent: (AnyEvent) -> Unit,
onNavigate: (Screen) -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                PavlovTopBar(sharedState, onEvent = { onEvent(it) })
                RankAndXpBar(sharedState) // XP bar just below top bar
            }
        },
        bottomBar = {
            PavlovNavbar(
                activeScreen = sharedState.activeScreen,
                onNavigate = onNavigate
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
        }
    }
}
