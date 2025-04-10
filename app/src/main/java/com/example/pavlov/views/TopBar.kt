package com.example.pavlov.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pavlov.R
import com.example.pavlov.theme.ThemeSwitch
import com.example.pavlov.utils.Vec2
import com.example.pavlov.viewmodels.SharedEvent
import com.example.pavlov.viewmodels.SharedState
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.height



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PavlovTopBar(
    sharedState: SharedState,
    onEvent: (SharedEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sharedState.activeScreen.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp)) // spacing between title and XP bar

                LinearProgressIndicator(
                    progress = sharedState.currentXp.toFloat() / sharedState.maxXp.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = "${sharedState.treats}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.dog_treat),
                    contentDescription = "Total Treats",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(24.dp)
                        .onGloballyPositioned {
                            if (it.isAttached) {
                                val bounds = it.boundsInRoot()
                                val off = it.positionInRoot()
                                onEvent(
                                    SharedEvent.SetCollectablesTarget(
                                        Vec2(
                                            x = (off.x + bounds.width / 2),
                                            y = (off.y + bounds.height / 2),
                                        )
                                    )
                                )
                            }
                        },
                )
            }
        },
        modifier = modifier
    )
}
