package com.example.pavlov.views

import android.util.Log
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pavlov.viewmodels.AnyEvent
import com.example.pavlov.viewmodels.PachinkoUiEvent
import com.example.pavlov.viewmodels.PachinkoViewModel
import com.example.pavlov.viewmodels.SharedState
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun PachinkoView(sharedState: SharedState, onEvent: (AnyEvent) -> Unit) {
    val vm = viewModel<PachinkoViewModel>()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PachinkoGameView(context, vm)
            },
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .width(120.dp)
                .height(360.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            PachinkoLaunchController(
                onPullReleased = {
                    vm.sendEvent(PachinkoUiEvent.LaunchBall(it))
                }
            )
        }
    }
}

@Composable
fun PachinkoLaunchController(
    onPullReleased: (launchPower: Float) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offset = remember {
        androidx.compose.animation.core.Animatable(
            0f
        )
    }
    var parentHeightPx by remember { mutableFloatStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                parentHeightPx = size.height.toFloat()
            }
            .pointerInput(Unit) {
                val decay = splineBasedDecay<Float>(this)
                detectVerticalDragGestures(
                    onDragStart = {},
                    onVerticalDrag = { change, dragAmount ->
                        coroutineScope.launch {
                            offset.snapTo(offset.value + dragAmount)
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        val pullFraction = if (parentHeightPx > 0) {
                            min(1f, offset.value / parentHeightPx)
                        } else {
                            0f
                        }
                        onPullReleased(pullFraction)
                        coroutineScope.launch {
                            offset.animateTo(targetValue = 0f, animationSpec = SpringSpec())
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            offset.animateTo(targetValue = 0f, animationSpec = SpringSpec())
                        }
                    }
                )
            }
    ) {
        Box(modifier = Modifier
            .fillMaxWidth(0.2f)
            .align(Alignment.TopCenter)
            .height(offset.value.dp / LocalDensity.current.density)
            .background(MaterialTheme.colorScheme.error)
        )
        Box(modifier = Modifier
            .offset { IntOffset(0, offset.value.roundToInt()) }
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
            .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Preview
@Composable
fun TestLaunchController() {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(360.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        PachinkoLaunchController(
            onPullReleased = {
                Log.d("LAUNCH", "Power = $it")
            }
        )
    }
}
