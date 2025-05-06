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
//import com.example.pavlov.theme.ThemeSwitch
import com.example.pavlov.utils.Vec2
import com.example.pavlov.viewmodels.SharedEvent
import com.example.pavlov.viewmodels.SharedState
import androidx.compose.foundation.layout.Column
//import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.background
//import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.draw.clip
import androidx.compose.animation.core.*
import com.example.pavlov.theme.Retro
import com.example.pavlov.utils.getRank
import com.example.pavlov.utils.getCurrentRankStartXp
import com.example.pavlov.utils.getNextRankXP
import com.airbnb.lottie.compose.*
// androidx.compose.ui.text.font.FontWeight
//import com.example.pavlov.theme.NablaFont
//import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import com.example.pavlov.utils.getTitleForXp
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext




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
            CircularXpLevelIndicator(sharedState = sharedState)
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
                    fontSize = 13.sp,
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
fun ShimmeringXpCircleWithPulse(progress: Float, rank: Int) {
//    val isLevelUp = progress >= 1f
    var lastRank by remember { mutableStateOf(rank) }
    val isLevelUp = lastRank != rank

    val pulseScale by animateFloatAsState(
        targetValue = if (isLevelUp) 1.5f else 1f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "pulse"
    )
    LaunchedEffect(rank) {
        if (isLevelUp){
            lastRank = rank
        }
    }

    val shimmerX by rememberInfiniteTransition(label = "shimmer").animateFloat(
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
            Color(0xFFFFD700),
            Color(0xFFFFFF00),
            Color(0xFFFFD700)
        ),
        start = Offset(shimmerX, 0f),
        end = Offset(shimmerX + 200f, 0f)
    )

    Canvas(modifier = Modifier.size((60 * pulseScale).dp)) {
        // Glow
        drawCircle(
            color = Color(0xFFFFFF00).copy(alpha = 0.25f),
            radius = size.minDimension / 2f + 8f
        )

        // Background circle
        drawArc(
            color = Color.DarkGray,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 8f)
        )

        // Animated XP shimmer progress
        drawArc(
            brush = shimmerBrush,
            startAngle = -90f,
            sweepAngle = progress * 360f,
            useCenter = false,
            style = Stroke(width = 8f)
        )
    }
}

@Composable
fun PlayLevelUpSound(rank: Int) {
    val context = LocalContext.current
    var lastRank by remember { mutableStateOf(rank) }
    val mediaPlayer = remember { mutableStateOf<MediaPlayer?>(null) }

    LaunchedEffect(rank) {
        if (rank > lastRank) {
            mediaPlayer.value?.release() // Clean up any existing player
            mediaPlayer.value = MediaPlayer.create(context, R.raw.level_up).apply {
                setOnCompletionListener {
                    it.release()
                    mediaPlayer.value = null
                }
                start()
            }
            lastRank = rank
        }
    }
}







@Composable
fun CircularXpLevelIndicator(sharedState: SharedState) {
    val context = LocalContext.current
    val currentXp = sharedState.currentXp
    val rank = getRank(currentXp)
    val rankStartXp = getCurrentRankStartXp(currentXp)
    val nextRankXp = getNextRankXP(currentXp)
    val title = getTitleForXp(currentXp)

    val xpInCurrentRank = (currentXp - rankStartXp).coerceAtLeast(0)
    val xpToNextRank = (nextRankXp - rankStartXp).coerceAtLeast(1)
    val xpProgress = xpInCurrentRank.toFloat() / xpToNextRank.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = xpProgress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "Animated XP Progress"
    )

    PlayLevelUpSound(rank)


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .height(IntrinsicSize.Min)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(50.dp)) {
                ShimmeringXpCircleWithPulse(progress = animatedProgress, rank = rank)
                Text(
                    text = "$rank",
                    fontSize = 20.sp,
                    fontFamily = Retro,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            fontSize = 15.sp,
            fontFamily = Retro,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}







//@Composable
//fun LiveLottiePet() {
//    val composition by rememberLottieComposition(
//        LottieCompositionSpec.Asset("dog_animation.json")
//    )
//
//    if (composition != null) {
//        Log.d("Lottie", "Composition loaded!")
//    } else {
//        Log.d("Lottie", "Composition is NULL")
//    }
//
//    val progress by animateLottieCompositionAsState(
//        composition,
//        iterations = LottieConstants.IterateForever
//    )
//
//    if (composition != null) {
//        LottieAnimation(
//            composition = composition,
//            progress = { progress },
//            modifier = Modifier.size(90.dp)
//        )
//    }
//}
