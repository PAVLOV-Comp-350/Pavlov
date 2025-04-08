package com.example.pavlov.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import com.example.pavlov.models.ScratcherCell
import com.example.pavlov.models.ScratcherGameState
import com.example.pavlov.theme.CasinoTheme
import com.example.pavlov.viewmodels.ScratcherEvent

/**
 * scratcher game UI component
 */
@Composable
fun ScratcherGame(
    gameState: ScratcherGameState,
    onEvent: (ScratcherEvent) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Game title
            Text(
                text = "Scratcher",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Game instructions
            Text(
                text = "Scratch to reveal prizes!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Scratcher grid
            ScratcherGrid(
                cells = gameState.cells,
                onScratch = { index -> onEvent(ScratcherEvent.ScratchCell(index)) },
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Results section
            if (gameState.isComplete) {
                ScratcherResults(
                    totalPrize = gameState.totalPrize,
                    onCollect = { onEvent(ScratcherEvent.CollectPrize) }
                )
            } else {
                Text(
                    text = "Scratch all cells to reveal your prize!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Close button
            TextButton(
                onClick = { onEvent(ScratcherEvent.CloseGame) }
            ) {
                Text("Close Game")
            }
        }
    }
}

@Composable
fun ScratcherGrid(
    cells: List<ScratcherCell>,
    onScratch: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(CasinoTheme.GoldGradient),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (row in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    if (index < cells.size) {
                        ScratcherCell(
                            cell = cells[index],
                            onScratch = { onScratch(index) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScratcherCell(
    cell: ScratcherCell,
    onScratch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scratchPaths = remember { mutableStateListOf<Path>() }
    var scratchProgress by remember { mutableFloatStateOf(0f) }
    val scratchThreshold = 0.6f
    var isRevealed by remember { mutableStateOf(cell.isRevealed) }

    // Mark as revealed once enough is scratched
    LaunchedEffect(scratchProgress) {
        if (scratchProgress >= scratchThreshold && !isRevealed) {
            isRevealed = true
            onScratch()
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Prize background with reward value
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (cell.value > 0) {
                            CasinoTheme.GoldGradient
                        } else {
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (cell.value > 0) {
                    Text(
                        text = "${cell.value}",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.dog_treat),
                        contentDescription = "treats",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "0",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val x = change.position.x
                        val y = change.position.y
                        val path = Path().apply {
                            moveTo(x, y)
                            lineTo(x + 1f, y + 1f)
                        }
                        scratchPaths.add(path)
                        scratchProgress += 0.01f
                        change.consume()
                    }
                }
        ) {
            if (!isRevealed) {
                // Draw silver scratch layer
                drawRect(
                    brush = Brush.linearGradient(CasinoTheme.SilverGradient)
                )

                if (scratchProgress < 0.2f) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val questionMarkPath = Path().apply {
                        moveTo(centerX - 15f, centerY - 20f)
                        cubicTo(
                            centerX - 15f, centerY - 35f,
                            centerX + 15f, centerY - 35f,
                            centerX + 15f, centerY - 20f
                        )
                        lineTo(centerX, centerY)
                        moveTo(centerX, centerY + 10f)
                        lineTo(centerX, centerY + 15f)
                    }
                    drawPath(
                        path = questionMarkPath,
                        color = Color.White,
                        style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                // erase parts to show prize underneath
                scratchPaths.forEach { path ->
                    drawPath(
                        path = path,
                        color = Color.Transparent,
                        style = Stroke(width = 80f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                        blendMode = androidx.compose.ui.graphics.BlendMode.Clear
                    )
                }
            }
        }
    }
}

        @Composable
        fun ScratcherResults(
            totalPrize: Int,
            onCollect: () -> Unit
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(500))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (totalPrize > 0) "You won!" else "Sorry, better luck next time!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (totalPrize > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )

                        if (totalPrize > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$totalPrize",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.dog_treat),
                                contentDescription = "treats",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    if (totalPrize > 0) {
                        Button(
                            onClick = onCollect,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CasinoTheme.PlayButtonColor
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Collect Prize")
                        }
                    }
                }
            }
        }

