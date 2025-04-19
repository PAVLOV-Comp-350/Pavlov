package com.example.pavlov.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
 * A unified win notification that appears when the player wins in casino games
 */
@Composable
fun WinNotification(
    prizeAmount: Int,
    onComplete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }

    LaunchedEffect(prizeAmount) {
        if (prizeAmount > 0) {
            PavlovApplication.addTreats(prizeAmount)
        }

        delay(2500)

        showDialog = false

        onComplete()
    }

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
                                colors = CasinoTheme.GoldGradient.map { it.copy(alpha = 0.9f) }
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
                            text = "YOU WIN!",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = "$prizeAmount",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Icon(
                                painter = painterResource(id = R.drawable.dog_treat),
                                contentDescription = "Treats",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Text(
                            text = "Treats added to your balance!",
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(1500))
                        ) {
                        }
                    }
                }
            }
        }
    }
}