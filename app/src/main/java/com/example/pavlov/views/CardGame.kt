package com.example.pavlov.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pavlov.models.CardGameState
import com.example.pavlov.viewmodels.CardEvent

/**
 *  Cards UI
 */

@Composable
fun CardGame(
    gameState: CardGameState,
    onEvent: (CardEvent) -> Unit,
    modifier: Modifier = Modifier
) {

}