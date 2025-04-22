package com.example.pavlov.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.withMatrix
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pavlov.R
import com.example.pavlov.utils.Vec2
import com.example.pavlov.viewmodels.AnyEvent
import com.example.pavlov.viewmodels.SharedState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Circle
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.MassType
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

sealed class PachinkoBody : Body()
{
    abstract fun render(canvas: Canvas)
}

class PachinkoBall(
    val res: Bitmap,
    val pos: Vector2 = Vector2(.0,.0)
) : PachinkoBody() {
    init {
        val bf = addFixture(Geometry.createCircle(1.0), 8000.0, 0.16, 0.7)
        bf.restitutionVelocity = 0.001
        setMass(MassType.NORMAL)
        translate(pos)
    }

    override fun render(canvas: Canvas) {
        val ballRadius = (fixtures[0].shape as Circle).radius.toFloat()
        val ballCenter = transform.translation.toVec2()
        canvas.drawBitmapCentered(res, pos = ballCenter, size = Vec2(ballRadius))
    }
}

class PachinkoPeg(
    val res: Bitmap,
    val pos: Vector2 = Vector2(.0,.0)
) : PachinkoBody() {
    init {
        val pf = addFixture(Geometry.createCircle(0.70))
        pf.restitution = 0.9
        setMass(MassType.INFINITE)
        translate(pos)
    }

    override fun render(canvas: Canvas) {
        val pegRadius = (fixtures[0].shape as Circle).radius.toFloat()
        val pegCenter = transform.translation.toVec2()
        canvas.drawBitmapCentered(res, pos = pegCenter, size = Vec2(pegRadius))
    }
}

class DebugMarker(
    val color: Int = Color.RED
) : PachinkoBody() {
    override fun render(canvas: Canvas) {
        val pos = transform.translation.toVec2()
        val paint = Paint()
        paint.color = color
        canvas.drawCircle(pos.x, pos.y, 50f, paint)
    }

}

private fun Canvas.drawBitmapCentered(bmp: Bitmap,
                                      pos: Vec2,
                                      size: Vec2,
                                      src: Rect? = null,
                                      paint: Paint? = null) {
    drawBitmap(
        bmp,
        src,
        RectF(
            pos.x - size.x,
            pos.y - size.y,
            pos.x + size.x,
            pos.y + size.y,
        ),
        paint
    )
}

private fun Vector2.toVec2(): Vec2 {
    return Vec2(this.x.toFloat(), this.y.toFloat())
}

private operator fun Vector2.times(j: Double): Vector2 {
    return Vector2(this.x * j, this.y * j)
}

private operator fun Vector2.plus(other: Vector2): Vector2 {
    return this.sum(other)
}


sealed interface PachinkoUiEvent {
    data class LaunchBall(val power: Float) : PachinkoUiEvent
}

class PachinkoGameView(
    context: Context,
    val vm: PachinkoViewModel,
) : SurfaceView(context), SurfaceHolder.Callback {
    private val steelBall: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.steel_ball)
    private val goldenPeg: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.gold_peg)
    private var worldToCanvas = Matrix()
    private var canvasToWorld = Matrix()

    // TODO: Constrain world bounds
    private val world = World<PachinkoBody>()
    private var gameThread: GameThread? = null
    private var gameCoroutineScope: CoroutineScope? = null

    inner class GameThread : Thread() {
        private var looper: Looper? = null
        private var handler: Handler? = null
        // This is basically a thread-safe fence to wait in the gameDispatcher to be initialized
        private val gameDispatcherReady = CountDownLatch(1)
        private lateinit var gameDispatcher: CoroutineDispatcher


        override fun run() {
            // 1. Prepare the Looper for this thread
            Looper.prepare()
            looper = Looper.myLooper()
            // 2. Create the Handler associated with this Looper
            handler = Handler(looper!!)
            // 3. Create the CoroutineDispatcher from the Handler
            // This dispatcher will post coroutine tasks to the Handler's message queue
            gameDispatcher = handler!!.asCoroutineDispatcher("GameThreadDispatcher")
            // Signal that the handler and dispatcher are ready
            gameDispatcherReady.countDown()
            // 4. Start the Looper - this makes the thread process messages
            // The loop() call blocks until Looper.quit() or quitSafely() is called
            Looper.loop()

            Log.e("GameThread", "GameThread loop finished")
        }

        fun waitForGameDispatcher(): CoroutineDispatcher {
            gameDispatcherReady.await()
            return gameDispatcher
        }

        fun quitSafely() {
            handler?.post{
                looper?.quitSafely()
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread = GameThread()
        gameThread?.start()

        // FIXME: USE ACTIVITY LIFECYCLE SCOPE!!!!!
        // NOTE: We need to launch this as a coroutine scope because we have to wait for the
        // GameThread to prepare the gameDispatcher which is a blocking operation
        GlobalScope.launch(Dispatchers.IO) {
            val thread = gameThread ?: return@launch

            val gameDispatcher = thread.waitForGameDispatcher()

            val gameJob = SupervisorJob()
            gameCoroutineScope = CoroutineScope(gameDispatcher + gameJob)

            // Event Collector Coroutine
            gameCoroutineScope?.launch {
                vm.uiEventChannel.collect {
                    handleUiEvent(it)
                }
            }
            // Main Game Loop Coroutine
            gameCoroutineScope?.launch {
                var lastFrameTime = System.nanoTime()
                while (isActive) {
                    val currentTime = System.nanoTime()
                    val deltaTimeInSeconds = (currentTime - lastFrameTime) / 1_000_000_000.0
                    update(deltaTimeInSeconds)
                    val canvas = holder.lockCanvas()
                    if (canvas != null) {
                        canvas.drawColor(Color.BLACK)
                        render(canvas)
                        holder.unlockCanvasAndPost(canvas)
                    }
                    lastFrameTime = currentTime
                    delay(TARGET_FRAME_TIME)
                }
            }
        }
    }

    private fun handleUiEvent(event: PachinkoUiEvent) {
        when(event) {
            is PachinkoUiEvent.LaunchBall -> {
                val b = PachinkoBall(steelBall)
                val launchPower = 50000.0
                b.setLinearVelocity(.0, launchPower * event.power)
                world.addBody(b)
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        val density = context.resources.displayMetrics.density
        updateTransformationMatrices(Size(width.toFloat(), height.toFloat()), density)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameCoroutineScope?.cancel()
        gameCoroutineScope = null

        gameThread?.quitSafely()
        var retry = true
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                // Retry
            }
        }
        gameThread = null
    }


    init {
        holder.addCallback(this)
        initializeGameWorld(width, height)
    }

    /**
     * Place pins in an arc relative to some anchor point
     * @param anchorPoint The center of the arc.
     * @param startTheta The angle in radians where the first peg will be placed.
     * @param endTheta The end angle in radians where the last peg will be placed.
     * @param displacement How far from the anchor point to place each peg.
     * */
    private fun placePinsInArc(
        anchorPoint: Vector2,
        numPins: Int,
        startTheta: Double,
        endTheta: Double,
        displacement: Double,
    ) {
        if (numPins == 0) return

        val arcLen = endTheta - startTheta
        val offset = Vector2(1.0, 0.0)
        offset.setMagnitude(displacement)
        offset.rotate(startTheta)

        val numGaps = numPins - 1
        if (numGaps == 0) {
            val theta = (arcLen / 2.0)
            offset.rotate(theta)
            val newPos = anchorPoint + offset
            val peg = PachinkoPeg(goldenPeg, newPos)
            world.addBody(peg)
        } else {
            val arcAdvance = arcLen / numGaps.toDouble()
            for (v in 0..<numPins) {
                val newPos = anchorPoint + offset
                val peg = PachinkoPeg(goldenPeg, newPos)
                world.addBody(peg)
                offset.rotate(arcAdvance)
            }
        }

    }


    private fun initializeGameWorld(width: Int, height: Int) {
        world.gravity.set(Vector2(0.0, -400.0))

        for (v in 0..20) {
            val ball = PachinkoBall(steelBall,
            Vector2(
                Random.nextDouble(-2.0, 2.0),
                Random.nextDouble(-2.0, 2.0)
            )
            )
            world.addBody(ball)
        }

//        placePinsInArc(
//            anchorPoint = Vector2(0.0, -20.0),
//            numPins = 10,
//            startTheta = .0,
//            endTheta = PI,
//            displacement = 6.0,
//        )

        placePinsInArc(
            anchorPoint = Vector2(0.0, -20.0),
            numPins = 30,
            startTheta = .0,
            endTheta = -PI,
            displacement = 16.0,
        )

//        for (v in 0..30) {
//            placePinInWorld(
//                world,
//                Random.nextDouble(-20.0, 20.0),
//                Random.nextDouble(-40.0, 0.0)
//            )
//        }


    }

    private fun updateTransformationMatrices(size: Size, density: Float) {
        val scale = PIXELS_PER_METER * density;
        worldToCanvas.reset()
        worldToCanvas.postScale(scale, -scale)
        worldToCanvas.postTranslate(size.width * 0.5f, size.height * 0.5f)
        if(!worldToCanvas.invert(canvasToWorld)) {
            Log.e("Matrix Inversion Failed", "")
        }
    }

    fun update(deltaTime: Double) {
        world.update(deltaTime)
    }

    fun render(canvas: Canvas) {
        with(canvas) {
            withMatrix(worldToCanvas) {
                for (body in world.bodies) {
                    body.render(canvas)
                }
            }
        }
    }

    companion object {
        const val PIXELS_PER_METER = 10;
        val TARGET_FRAME_TIME = 16.milliseconds // Target ~60 FPS
    }

}

class PachinkoViewModel : ViewModel() {
    private val _uiEventChannel = Channel<PachinkoUiEvent>()
    val uiEventChannel = _uiEventChannel.receiveAsFlow()

    fun sendEvent(event: PachinkoUiEvent) {
        viewModelScope.launch {
            _uiEventChannel.send(event)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _uiEventChannel.close()
    }
}

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
//                    Log.d("LAUNCH", "Power = $it")
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

@Preview @Composable
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