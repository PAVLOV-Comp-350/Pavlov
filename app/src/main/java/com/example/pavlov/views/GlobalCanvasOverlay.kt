package com.example.pavlov.views

import android.widget.ImageButton
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.pavlov.R
import com.example.pavlov.viewmodels.SharedEvent
import com.example.pavlov.viewmodels.SharedState
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

// In the layout hierarchy this element is placed such that it is over top of the default UI.
// This allows us to use UI offsets in our rendering code to create particle effects.
@Composable
fun GlobalCanvasOverlay(state: SharedState, onEvent: (SharedEvent) -> Unit) {
    // Periodically update rewards in the sharedViewModel every 16ms
    LaunchedEffect(Unit) {
        while (true) {
            onEvent(SharedEvent.UpdateRewardCollectables)
            delay(16)
        }
    }

    val treatSizePx = with(LocalDensity.current) { 24.dp.toPx() }
    val treatBMP = painterResource(id = R.drawable.dog_treat)
        .toImageBitmap(
            size = Size(treatSizePx, treatSizePx),
        )
    Canvas(modifier = Modifier.fillMaxSize()) {
        state.rewardCollectables.forEach{
            drawImage(
                image = treatBMP,
                topLeft = Offset(
                    x = (it.pos.x - treatBMP.width * 0.5).toFloat(),
                    y = (it.pos.y - treatBMP.height * 0.5).toFloat(),
                ),
            )
        }
    }
}

fun Painter.toImageBitmap(
    density: Density = Density(density = 1f),
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    size: Size = intrinsicSize,
    config: ImageBitmapConfig = ImageBitmapConfig.Argb8888,
): ImageBitmap {
    val image = ImageBitmap(width = size.width.roundToInt(), height = size.height.roundToInt(), config = config)
    val canvas = androidx.compose.ui.graphics.Canvas(image)
    CanvasDrawScope().draw(density = density, layoutDirection = layoutDirection, canvas = canvas, size = size) {
        draw(size = this.size)
    }
    return image
}