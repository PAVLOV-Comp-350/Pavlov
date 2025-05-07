package com.example.pavlov.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pavlov.R
import com.example.pavlov.models.CasinoGame
import com.example.pavlov.theme.CasinoTheme

/**
 * Dialog that appears when a game is selected
 */
@Composable
fun GameDialog(
    game: CasinoGame,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    availableTreats: Int
) {
    val canPlay = game.costInTreats?.let { cost ->
        availableTreats >= cost
    } ?: true

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameDialogHeader(game)

                CustomDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                GameDialogContent(
                    game = game,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )


                GameDialogCostInfo(game, availableTreats, canPlay)

                Spacer(modifier = Modifier.height(12.dp))

                GameDialogActions(game, onDismiss, onPlay, canPlay)

                if (!canPlay) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Not enough treats to play!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun GameDialogHeader(game: CasinoGame) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(game.gradient)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (game.iconResId != null) {
                Image(
                    painter = painterResource(id = game.iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    colorFilter = null
                )
            } else if (game.iconVector != null) {
                Icon(
                    imageVector = game.iconVector,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = game.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = game.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GameDialogContent(
    game: CasinoGame,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(game.gradient),
                alpha = 0.8f
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (game.iconResId != null) {
            Image(
                painter = painterResource(id = game.iconResId),
                contentDescription = game.name,
                modifier = Modifier.fillMaxSize(0.7f),
                colorFilter = null
            )
        } else if (game.iconVector != null) {
            Icon(
                imageVector = game.iconVector,
                contentDescription = game.name,
                tint = Color.White,
                modifier = Modifier.fillMaxSize(0.6f)
            )
        } else {
            Text(
                text = "INSERT GAME HERE",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GameDialogCostInfo(
    game: CasinoGame,
    availableTreats: Int,
    canPlay: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            CasinoTheme.getTreatIndicatorColor(),
                            CasinoTheme.getTreatIndicatorColor().copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "$availableTreats",
                color = MaterialTheme.colorScheme.onTertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                painter = painterResource(id = R.drawable.dog_treat),
                contentDescription = "treats",
                tint = Color.Unspecified,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        game.costInTreats?.let { cost ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (canPlay)
                    CasinoTheme.getTreatIndicatorColor()
                else
                    MaterialTheme.colorScheme.error
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Cost: $cost",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.dog_treat),
                        contentDescription = "treats",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GameDialogActions(
    game: CasinoGame,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    canPlay: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Close")
        }

        Button(
            onClick = onPlay,
            enabled = canPlay,
            modifier = Modifier.weight(1.25f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (canPlay)
                    CasinoTheme.PlayButtonColor
                else
                    MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Play",
                    fontWeight = FontWeight.Bold
                )
                game.costInTreats?.let { cost ->
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("($cost")
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.dog_treat),
                        contentDescription = "treats",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(")")
                }
            }
        }
    }
}