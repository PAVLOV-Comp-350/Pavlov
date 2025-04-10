package com.example.pavlov.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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

            TextButton(
                onClick = { onEvent(RouletteEvent.Spin) }
            ) {
                Text("Spin")
            }
            //fill game stuff here

            RouletteBettingBoard(
                onEvent = { pickEvent ->
                    onEvent(pickEvent)
                },
                modifier = Modifier
            )

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
    onEvent: (RouletteEvent.SelectPick) -> Unit,
    modifier: Modifier = Modifier
) {
    val roulettePicks = listOf("1 - 12", "13 - 24", "25 - 36", "00") + (0..36).map { it.toString() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =  Arrangement.spacedBy(8.dp)
    ) {
        roulettePicks.chunked(6).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                rowItems.forEach { label ->
                    val index = roulettePicks.indexOf(label)
                    TextButton(
                        onClick = { onEvent(RouletteEvent.SelectPick(index))},
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = CasinoTheme.EmeraldGradient
                                )
                            )
                    ) {
                        Text(label)
                    }
                }
            }
        }
    }
}