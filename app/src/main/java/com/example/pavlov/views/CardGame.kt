package com.example.pavlov.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

@Composable
fun CardHand(
    hand: List<String>,
    heldCards: List<String>,
    onCardHold: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        hand.forEach{ card ->
            val isHeld = heldCards.contains(card)
            TextButton(
                onClick = { onCardHold(card)},
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        color = if (isHeld) Color.Green else Color.Gray,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Text(card, color = Color.White)
            }
        }
    }
}