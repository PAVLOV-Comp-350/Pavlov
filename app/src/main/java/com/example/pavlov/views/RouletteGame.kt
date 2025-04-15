package com.example.pavlov.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pavlov.R
import com.example.pavlov.models.RouletteGameState
import com.example.pavlov.theme.CasinoTheme
import com.example.pavlov.viewmodels.RouletteEvent
import com.example.pavlov.viewmodels.ScratcherEvent
import kotlinx.coroutines.delay


/**
 *  Roulette UI
 */

@Composable
fun RouletteGame(
    gameState: RouletteGameState,
    onEvent: (RouletteEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Game title
            Text(
                text = "Roulette",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )


            Text(
                text = "Select your Pick, then Spin to Win!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SpinningWheel(isSpinning = gameState.isSpinning)

            TextButton(
                onClick = { onEvent(RouletteEvent.Spin) }
            ) {
                Text("Spin")
            }
            //fill game stuff here

            RouletteBettingBoard(
                selectedIndex = gameState.pickIndex,
                onEvent = { pickEvent ->
                    onEvent(pickEvent)
                },
                modifier = Modifier
            )

            if(gameState.resultMessage.isNotBlank()) {
                Text(
                    text = gameState.resultMessage,
                    color = if (gameState.totalPrize > 0) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            TextButton(
                onClick = { onEvent(RouletteEvent.CloseGame) }
            ) {
                Text("Close Game")
            }
        }
    }
}

@Composable
fun RouletteBettingBoard(
    selectedIndex: Int,
    onEvent: (RouletteEvent.SelectPick) -> Unit,
    modifier: Modifier = Modifier
) {
    val roulettePicks = listOf("1 - 12", "13 - 24", "25 - 36", "00") + (0..36).map { it.toString() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =  Arrangement.spacedBy(6.dp)
    ) {
        roulettePicks.chunked(6).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                rowItems.forEach { label ->
                    val index = roulettePicks.indexOf(label)
                    val isSelected = index == selectedIndex
                    TextButton(
                        onClick = { onEvent(RouletteEvent.SelectPick(index))},
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = if (isSelected) {
                                        CasinoTheme.EmeraldGradient.map { it.copy(alpha = 0.5f)}
                                    } else {
                                        CasinoTheme.EmeraldGradient
                                    }
                                )
                            )
                    ) {
                        Text(
                            label,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpinningWheel(
    isSpinning: Boolean,
    modifier: Modifier = Modifier
) {
    val rotation = remember { Animatable(0f) }

    // Launch the animation if the wheel is spinning
    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            // Animate rotation over 3 seconds
            rotation.animateTo(
                targetValue = rotation.value + 1440f,  // Rotate 360 degrees
                animationSpec = tween(
                    durationMillis = 3000,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    // Apply the rotation to the wheel's modifier
    Box(
        modifier = modifier
            .size(160.dp)
            .graphicsLayer(
                rotationZ = rotation.value // Apply rotation to the wheel
            )
    ) {
        // Add your roulette wheel image or shape here
        Image(
            painter = painterResource(id = R.drawable.roulette_wheel), // Use a wheel image
            contentDescription = "Roulette Wheel",
            modifier = Modifier.fillMaxSize()
        )
    }
}