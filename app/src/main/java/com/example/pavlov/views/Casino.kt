package com.example.pavlov.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pavlov.viewmodels.CasinoEvent
import com.example.pavlov.viewmodels.CasinoState
import com.example.pavlov.viewmodels.AnyEvent
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                // Header
                CasinoHeader(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Games List
                CasinoGamesList(
                    onGameSelected = { game ->
                        onEvent(CasinoEvent.SelectGame(game))
                    },
                )
            }
        }

        // Show game dialog when a game is selected
        state.selectedGame?.let { game ->
            when {
                game.name == "Scratcher" && state.scratcherGameState != null -> {
                        Dialog(
                            onDismissRequest = { onEvent(CasinoEvent.CloseGameDialog) },
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = false,
                                usePlatformDefaultWidth = false
                            )
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .fillMaxHeight(0.9f)
                                    .padding(8.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 8.dp
                                )
                            ) {
                                ScratcherGame(
                                    gameState = state.scratcherGameState,
                                    onEvent = { scratcherEvent ->
                                        onEvent(CasinoEvent.ScratcherEvent(scratcherEvent))
                                    }
                                )
                            }
                        }
                }

                game.name == "Roulette" && state.rouletteGameState !=null -> {
                        Dialog(
                            onDismissRequest = { onEvent(CasinoEvent.CloseGameDialog) },
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = false,
                                usePlatformDefaultWidth = false
                            )
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth( 0.95f)
                                    .fillMaxHeight(0.9f)
                                    .padding(8.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 8.dp
                                )
                            ) {
                                RouletteGame(
                                    gameState = state.rouletteGameState,
                                    onEvent = { rouletteEvent ->
                                        onEvent(CasinoEvent.RouletteEvent(rouletteEvent))
                                    }
                                )
                            }
                        }
                    }

                game.name == "Cards" && state.cardGameState !=null -> {
                    Dialog(
                        onDismissRequest = { onEvent(CasinoEvent.CloseGameDialog) },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = false,
                            usePlatformDefaultWidth = false
                        )
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth( 0.95f)
                                .fillMaxHeight(0.9f)
                                .padding(8.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 8.dp
                            )
                        ) {
                            CardGame(
                                gameState = state.cardGameState,
                                onEvent = { cardEvent ->
                                    onEvent(CasinoEvent.CardEvent(cardEvent))
                                }
                            )
                        }
                    }
                }

                else -> {
                    GameDialog(
                        game = game,
                        onDismiss = { onEvent(CasinoEvent.CloseGameDialog) },
                        onPlay = {
                            // Handles playing the game
                            game.costInTreats?.let { cost ->
                                if (sharedState.treats >= cost) {
                                    onEvent(CasinoEvent.SpendTreats(cost))
                                    onEvent(CasinoEvent.PlayGame(game))
                                }
                            }
                        },
                        availableTreats = sharedState.treats
                    )
                }
            }
        }
    }
}

        /**
         * Header section for the Casino screen
         */
        @Composable
        fun CasinoHeader(modifier: Modifier = Modifier) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Play games and win more treats",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                CustomDivider(
                    modifier = Modifier
                        .width(180.dp)
                        .padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            }
        }


