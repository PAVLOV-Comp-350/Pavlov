package com.example.pavlov.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pavlov.R
import com.example.pavlov.models.SlotsGameState
import com.example.pavlov.theme.CasinoTheme
import com.example.pavlov.viewmodels.SlotsEvent
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Slots machine game UI
 */
@Composable
fun SlotsGame(
    gameState: SlotsGameState,
    onEvent: (SlotsEvent) -> Unit,
    modifier: Modifier = Modifier,
    availableTreats: Int = 0

) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Slots",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Spin to Win! Match symbols for prizes!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Your treats: $availableTreats",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.dog_treat),
                    contentDescription = "treats",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }

            SlotMachineDisplay(
                gameState = gameState,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                val spinText = "Cost: ${gameState.spinCost}"
                val spinColor = if (availableTreats >= gameState.spinCost || gameState.freeFirstSpin)
                    MaterialTheme.colorScheme.onSurface
                else
                    Color.Red

                Text(
                    text = spinText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = spinColor
                )

                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.dog_treat),
                    contentDescription = "treats cost",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "per additional spin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = spinColor
                )
            }

            Button(
                onClick = { onEvent(SlotsEvent.Spin) },
                enabled = !gameState.isSpinning && (availableTreats >= gameState.spinCost || gameState.freeFirstSpin),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CasinoTheme.PlayButtonColor
                ),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
            ) {
                Text(
                    text = "SPIN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (availableTreats < gameState.spinCost && !gameState.isSpinning && !gameState.freeFirstSpin) {
                Text(
                    text = "Not enough treats to spin!",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (gameState.resultMessage.isNotBlank() && !gameState.isSpinning &&
                gameState.totalPrize == 0 && availableTreats < gameState.spinCost
            ) {
                Text(
                    text = "Not enough treats to continue playing!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            TextButton(
                onClick = { onEvent(SlotsEvent.CloseGame) }
            ) {
                Text("Close Game")
            }
        }
    }
    val showWinNotification =
        gameState.totalPrize > 0 && !gameState.isSpinning && gameState.resultMessage.isNotBlank()
    val showLoseNotification =
        gameState.totalPrize == 0 && !gameState.isSpinning && gameState.resultMessage.isNotBlank()

    if (showWinNotification) {
        WinNotification(
            prizeAmount = gameState.totalPrize,
            onComplete = {
                onEvent(SlotsEvent.CloseGame)
            }
        )
    }

    if (showLoseNotification) {
        LoseNotification(
            gameName = "Slots",
            playCost = gameState.spinCost,
            onTryAgain = {
                onEvent(SlotsEvent.RestartGame)
            },
            onClose = {
                onEvent(SlotsEvent.CloseGame)
            }
        )
    }
}

@Composable
fun SlotMachineDisplay(
    gameState: SlotsGameState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            )
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(CasinoTheme.GoldGradient),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (i in 0 until 3) {
                SlotReel(
                    symbols = gameState.reels[i],
                    isSpinning = gameState.spinningReels[i],
                    stopPosition = gameState.stopPosition[i],
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SlotReel(
    symbols: List<String>,
    isSpinning: Boolean,
    stopPosition: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "slot_spin")

    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_animation"
    )

    val offsetY = if (isSpinning) {
        animatedOffset
    } else {
        stopPosition.toFloat() / symbols.size.toFloat()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val visibleIndex = if (isSpinning) {
                ((offsetY * symbols.size).toInt()) % symbols.size
            } else {
                stopPosition
            }

            Text(
                text = symbols[visibleIndex],
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}