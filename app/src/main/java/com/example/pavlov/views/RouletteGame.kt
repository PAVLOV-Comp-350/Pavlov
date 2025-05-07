package com.example.pavlov.views

import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pavlov.R
import com.example.pavlov.models.RouletteGameState
import com.example.pavlov.models.SoundManager
import com.example.pavlov.theme.CasinoTheme
import com.example.pavlov.viewmodels.RouletteEvent
import com.example.pavlov.viewmodels.ScratcherEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin


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
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
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

            SpinningWheel(
                gameState = gameState,
                isSpinning = gameState.isSpinning,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            val context = LocalContext.current

            LaunchedEffect(Unit) {
                SoundManager.init(context)
            }

            TextButton(
                onClick = {
                    if (gameState.pick.isNotBlank()) {
                        onEvent(RouletteEvent.Spin)
                        SoundManager.playRouletteSound()
                    }
                },
                enabled = !gameState.isSpinning && gameState.pick.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CasinoTheme.PlayButtonColor
                ),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
            ) {
                Text(
                    text = "Spin",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            RouletteBettingBoardDropdown(
                selectedIndex = gameState.pickIndex,
                onEvent = { pickEvent ->
                    onEvent(pickEvent)
                },
                modifier = Modifier
            )

            if (gameState.pick.isBlank() && !gameState.isSpinning) {
                Text(
                    text = "Please select a number first",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
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

    val showWinNotification =
        gameState.totalPrize > 0 && !gameState.isSpinning && gameState.resultMessage.isNotBlank()
    val showLoseNotification =
        gameState.totalPrize == 0 && !gameState.isSpinning && gameState.resultMessage.isNotBlank()

    if (showWinNotification) {
        WinNotification(
            prizeAmount = gameState.totalPrize,
            onComplete = {
                onEvent(RouletteEvent.CloseGame)
            }
        )
    }

    if (showLoseNotification) {
        LoseNotification(
            gameName = "Roulette",
            playCost = 8,
            onTryAgain = {
                onEvent(RouletteEvent.RestartGame)
            },
            onClose = {
                onEvent(RouletteEvent.CloseGame)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouletteBettingBoardDropdown(
    selectedIndex: Int,
    onEvent: (RouletteEvent.SelectPick) -> Unit,
    modifier: Modifier = Modifier
) {
    val roulettePicks = listOf("Red", "Black", "1 - 12", "13 - 24", "25 - 36", "00") + (0..36).map { it.toString() }
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = roulettePicks.getOrNull(selectedIndex) ?: "Select a Pick"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            readOnly = true,
            value = selectedLabel,
            onValueChange = {},
            label = { Text("Pick") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            roulettePicks.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onEvent(RouletteEvent.SelectPick(index))
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SpinningWheel(
    gameState: RouletteGameState,
    isSpinning: Boolean,
    modifier: Modifier = Modifier
) {
    val wheelOrder = listOf(
        "0", "28", "9", "26", "30", "11", "7", "20", "32", "17", "5", "22",
        "34", "15", "3", "24", "36", "13", "1", "00", "27", "10", "25", "29",
        "12", "8", "19", "31", "18", "6", "21", "33", "16", "4", "23", "35",
        "14", "2"
    )

    val winningNumber = listOf("00", "0") + (1..36).map {it.toString() }

    val redNumbers = setOf("1", "3", "5", "7", "9", "12", "14", "16", "18", "19", "21", "23", "25", "27", "30", "32", "34", "36")
    val blackNumbers = setOf("2", "4", "6", "8", "10", "11", "13", "15", "17", "20", "22", "24", "26", "28", "29", "31", "33", "35")

    val rotation = remember { Animatable(0f) }

    val lastSpinNumber = remember { mutableStateOf<String?>(null) }

    val winningIndexMapping: Map<String, Int> = winningNumber.associateWith { number ->
        wheelOrder.indexOf(number)
    }

    LaunchedEffect(gameState.win) {
        val currentWin = gameState.win
        if ( currentWin != lastSpinNumber.value && isSpinning) {
            // Get the correct index of the winning number from the mapping
            val winningIndex = winningIndexMapping[currentWin] ?: return@LaunchedEffect
            if (winningIndex == -1) return@LaunchedEffect // Handle invalid case

            lastSpinNumber.value = currentWin // Prevent future re-spins on same win

            val fullSpins = (3..6).random() // Random number of full spins
            val totalRotation = 360f * fullSpins // Total rotation for multiple spins

            val anglePerItem = 360f / wheelOrder.size
            val rawTargetAngle = winningIndex * anglePerItem + anglePerItem / 2 // Center the target on the winning item

            // Adjust the target angle to ensure proper alignment
            val targetAngle = (360f - (rawTargetAngle % 360f) - 90f) % 360f

            // Use 0 as the fixed start angle for predictability
            val startAngle = 0f // Fixed starting angle for the spin

            // Snap to the fixed start angle
            rotation.snapTo(startAngle)

            // Calculate the final target rotation: start angle + full spins + target angle
            val targetRotation = startAngle + totalRotation + targetAngle

            // Animate the wheel to the target position
            rotation.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = modifier.size(340.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(rotationZ = rotation.value)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                val sliceAngle = 360f / wheelOrder.size

                drawCircle(
                    color = Color.DarkGray,
                    radius = radius + 20f,
                    center = center,
                    style = Stroke(width = 10f)
                )

                wheelOrder.forEachIndexed { index, label ->
                    val startAngle = index * sliceAngle
                    val angleRad = Math.toRadians((startAngle + sliceAngle / 2).toDouble())

                    val endX = center.x + radius * cos(angleRad).toFloat()
                    val endY = center.y + radius * sin(angleRad).toFloat()

                    drawLine(
                        color = Color.White,
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 2f
                    )

                    val color = when (label) {
                        "0", "00" -> Color.hsv(120f, 0.4f, 0.7f)
                        in redNumbers -> Color.Red
                        in blackNumbers -> Color.Black
                        else -> Color.Gray
                    }

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sliceAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    drawCircle(
                        color = Color(0xFF4E342E),
                        radius = radius * 0.7f, // small center circle (adjust as needed)
                        center = center,
                        style = Fill
                    )

                    drawLine(
                        color = Color(0xFFFFA500),
                        start = Offset(center.x - radius * 0.15f, center.y),
                        end = Offset(center.x + radius * 0.15f, center.y),
                        strokeWidth = 20f
                    )
                    drawLine(
                        color = Color(0xFFFFA500),
                        start = Offset(center.x, center.y - radius * 0.15f),
                        end = Offset(center.x, center.y + radius * 0.15f),
                        strokeWidth = 20f
                    )

                    val textOffset = Offset(
                        x = center.x + (radius * 0.92f * cos(angleRad)).toFloat(),
                        y = center.y + (radius * 0.92f * sin(angleRad)).toFloat()
                    )

                    val paint = android.graphics.Paint().apply {
                        this.color = android.graphics.Color.WHITE
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 24f
                        isFakeBoldText = true
                    }

                    val angleDegrees = Math.toDegrees(angleRad).toFloat() + 90f
                    val nativeCanvas = drawContext.canvas.nativeCanvas

                    nativeCanvas.save()
                    nativeCanvas.rotate(angleDegrees, textOffset.x, textOffset.y)
                    nativeCanvas.drawText(label, textOffset.x, textOffset.y, paint)
                    nativeCanvas.restore()
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Pointer",
                tint = Color.Yellow,
                modifier = Modifier
                    .size(32.dp)
                    .padding(top = 4.dp)
            )

            gameState.win.let { win ->
                if (!gameState.isSpinning) {
                    Box(
                        modifier = Modifier
                            .padding(top = 36.dp)
                            .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Winner: $win",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}