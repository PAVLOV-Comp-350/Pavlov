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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.Offset
import com.example.pavlov.theme.Retro
import com.example.pavlov.utils.getRank
import com.example.pavlov.utils.getCurrentRankStartXp
import com.example.pavlov.utils.getNextRankXP
import com.airbnb.lottie.compose.*
// androidx.compose.ui.text.font.FontWeight
//import com.example.pavlov.theme.NablaFont
import android.util.Log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PavlovTopBar(
    sharedState: SharedState,
    onEvent: (SharedEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            // 🐶 Live Pet animation on the left
            LiveLottiePet()
        },
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {}

                Spacer(modifier = Modifier.height(6.dp)) // spacing between title and XP bar
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
                    fontFamily = Retro,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.dog_treat),
                    contentDescription = "Total Treats",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(30.dp)
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
                Spacer(modifier = Modifier.width(8.dp))

            }
        },
    )
}

@Composable
fun ShimmeringXpBar(progress: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val shimmerX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFFFFFF00), // Bright yellow
            Color(0xFFFFD700)
        ),
        start = Offset(shimmerX, 0f),
        end = Offset(shimmerX + 200f, 0f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.DarkGray) // <-- this is the background track
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(shimmerBrush) // <-- shimmer only on the progress bar
        )
    }
}



@Composable
fun RankAndXpBar(sharedState: SharedState) {
    val currentXp = sharedState.currentXp

    val rank = getRank(currentXp)
    val rankStartXp = getCurrentRankStartXp(currentXp)
    val nextRankXp = getNextRankXP(currentXp)

    val xpInCurrentRank = (currentXp - rankStartXp).coerceAtLeast(0)
    val xpToNextRank = (nextRankXp - rankStartXp).coerceAtLeast(1)
    val progress = xpInCurrentRank.toFloat() / xpToNextRank.toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "$rank",
                fontSize = 12.sp,
                fontFamily = Retro,
                color = Color.White
            )

        }

        Spacer(modifier = Modifier.height(4.dp))

        ShimmeringXpBar(progress = progress)
    }
}

@Composable
fun LiveLottiePet() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("dog_animation.json")
    )

    if (composition != null) {
        Log.d("Lottie", "Composition loaded!")
    } else {
        Log.d("Lottie", "Composition is NULL")
    }

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    if (composition != null) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(90.dp)
        )
    }
}
















