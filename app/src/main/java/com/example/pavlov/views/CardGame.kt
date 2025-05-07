package com.example.pavlov.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pavlov.models.CardGameState
import com.example.pavlov.theme.CasinoTheme
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Game Title
            Text(
                text = "Poker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            PrizeChart()

            // Game Instructions
            Text(
                text = "Tap cards to hold. Redraw to replace others!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Hand Display
            CardHand(
                hand = gameState.hand,
                heldCards = gameState.heldCards,
                onCardHold = { card ->
                    onEvent(CardEvent.HoldCard(card)) // You'll need to define this event
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            // Deal / Redraw Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                TextButton(
                    onClick = { onEvent(CardEvent.DealFromDeck) },
                    enabled = gameState.hand.isEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CasinoTheme.PlayButtonColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(50.dp)
                ) {
                    Text(
                        "Deal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = {
                        onEvent(CardEvent.Redraw)
                    },
                    enabled = gameState.hand.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CasinoTheme.PlayButtonColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp),

                    ) {
                    Text(
                        "Redraw",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            val showWinNotification =
                gameState.totalPrize > 0 && gameState.hasRedrawn && gameState.resultMessage.isNotBlank()
            val showLoseNotification =
                gameState.totalPrize == 0 && gameState.hasRedrawn && gameState.resultMessage.isNotBlank()

            if(showWinNotification) {
                WinNotification(
                    prizeAmount = gameState.totalPrize,
                    onComplete = {
                        onEvent(CardEvent.CloseGame)
                    }
                )
            }

            if(showLoseNotification) {
                LoseNotification(
                    gameName = "Poker",
                    playCost = gameState.dealCost,
                    onTryAgain = {
                        onEvent(CardEvent.RestartGame)
                    },
                    onClose = {
                        onEvent(CardEvent.CloseGame)
                    }
                )
            }

            // Close Game Button
            TextButton(
                onClick = { onEvent(CardEvent.CloseGame) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Close Game")
            }
        }
    }
}

@Composable
fun CardHand(
    hand: List<String>,
    heldCards: List<String>,
    onCardHold: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        hand.take(5).forEach { card ->
            val isHeld = heldCards.contains(card)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = { onCardHold(card) },
                    modifier = Modifier
                        .padding(2.dp)
                        .size(width = 60.dp, height = 75.dp)
                        .border(
                            width = if (isHeld) 3.dp else 1.dp,
                            color = if (isHeld) Color.Green else Color.Gray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = Color.Gray,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Text(
                        text = formatCardDisplay(card),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun PrizeChart() {
    val prizeMap = listOf(
        "Royal Flush" to 1000,
        "Straight Flush" to 500,
        "Four of a Kind" to 250,
        "Full House" to 100,
        "Flush" to 75,
        "Straight" to 50,
        "Three of a Kind" to 25,
        "Two Pair" to 10,
        "One Pair (Jacks+)" to 5
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Prize Chart",
            style = MaterialTheme.typography.titleLarge
        )

        prizeMap.forEachIndexed { index, (hand, prize) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = hand)
                Text(text = "$prize")
            }

            if (index != prizeMap.lastIndex) {
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun formatCardDisplay(card: String): String {
    val suitSymbol = when (card.last()) {
        'S' -> "♠"
        'H' -> "♥"
        'C' -> "♣"
        'D' -> "♦"
        else -> "?"
    }

    val value = card.dropLast(1) // e.g. "AS" -> "A"
    return "$value$suitSymbol"
}
