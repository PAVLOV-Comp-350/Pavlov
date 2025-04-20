package com.example.pavlov.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pavlov.PavlovApplication
import com.example.pavlov.R
import com.example.pavlov.theme.CasinoTheme
import kotlinx.coroutines.delay

/**
 * A notification that appears when the player loses in any casino game
 * with an option to try again - using Dialog to block interactions
 */
@Composable
fun LoseNotification(
    gameName: String,
    playCost: Int,
    onTryAgain: () -> Unit,
    onClose: () -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }
    val hasSufficientTreats = PavlovApplication.treats.collectAsState().value >= playCost


    if (showDialog) {
        Dialog(
            onDismissRequest = {
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = CasinoTheme.SilverGradient.map { it.copy(alpha = 0.9f) }
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "BETTER LUCK NEXT TIME!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "You didn't win anything this time.",
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Button(
                            onClick = {
                                showDialog = false
                                onTryAgain()
                            },
                            enabled = hasSufficientTreats,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CasinoTheme.PlayButtonColor,
                                disabledContainerColor = Color.Gray
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Try Again",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "($playCost",
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.width(2.dp))

                                Icon(
                                    painter = painterResource(id = R.drawable.dog_treat),
                                    contentDescription = "treats",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(14.dp)
                                )

                                Text(
                                    text = ")",
                                    fontSize = 14.sp
                                )
                            }
                        }

                        if (!hasSufficientTreats) {
                            Text(
                                text = "Not enough treats to play again!",
                                color = Color.Red,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        TextButton(
                            onClick = {
                                showDialog = false
                                onClose()
                            }
                        ) {
                            Text(
                                text = "Close",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}