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
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.withMatrix
import com.example.pavlov.R
import com.example.pavlov.utils.Vec2
import com.example.pavlov.viewmodels.AnyEvent
import com.example.pavlov.viewmodels.SharedState
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Circle
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.MassType
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World
import kotlin.math.PI
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

enum class PachinkoID {
    Ball,
    Peg
}

class PachinkoBody(
    val id: PachinkoID,
    val res: Bitmap? = null,
) : Body() {

    fun render(canvas: Canvas) {
        when (id) {
            PachinkoID.Ball -> {
                val ballRadius = (fixtures[0].shape as Circle).radius.toFloat()
                val ballCenter = transform.translation.toVec2()
                canvas.drawBitmapCentered(res!!, pos = ballCenter, size = Vec2(ballRadius))
            }
            PachinkoID.Peg -> {
                val pegRadius = (fixtures[0].shape as Circle).radius.toFloat()
                val pegCenter = transform.translation.toVec2()
                canvas.drawBitmapCentered(res!!, pos = pegCenter, size = Vec2(pegRadius))
            }
        }
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

class PachinkoGameView(
    context: Context,
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

    inner class GameThread(private val holder: SurfaceHolder) : Thread() {
        var running = true
        override fun run() {
            var lastFrameTime = System.nanoTime()
            while (running) {
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
                try {
                    sleep(TARGET_FRAME_TIME.inWholeMilliseconds)
                } catch (e: InterruptedException) {
                    Log.e("Thread Interrupted", e.toString())
                }
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread = GameThread(holder)
        gameThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        val density = context.resources.displayMetrics.density
        updateTransformationMatrices(Size(width.toFloat(), height.toFloat()), density)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                // Retry
            }
        }
    }

    fun pauseGame() {
        gameThread?.running = false
    }

    fun resumeGame() {
        gameThread?.running = true
    }

    init {
        holder.addCallback(this)
        initializeGameWorld(width, height)
    }

    private fun placePinInWorld(world: World<PachinkoBody>, x: Double, y: Double) {
        val peggle = PachinkoBody(PachinkoID.Peg, goldenPeg)
        val pf = peggle.addFixture(Geometry.createCircle(0.70))
        pf.restitution = 1.0
        peggle.setMass(MassType.INFINITE)
        peggle.translate(Vector2(x, y))
        world.addBody(peggle)
    }

    private fun initializeGameWorld(width: Int, height: Int) {
        world.gravity.set(Vector2(0.0, -400.0))

        for (v in 0..20) {
            val ball = PachinkoBody(PachinkoID.Ball, steelBall)
            val bf = ball.addFixture(Geometry.createCircle(1.0), 10000.0, 0.08, 1.0)
            bf.restitutionVelocity = 0.001
            ball.setMass(MassType.NORMAL)
            ball.translate(
                Random.nextDouble(-2.0, 2.0),
                Random.nextDouble(-2.0, 2.0)
            )
            world.addBody(ball)
        }

//        for (v in 0..30) {
//            placePinInWorld(
//                world,
//                Random.nextDouble(-20.0, 20.0),
//                Random.nextDouble(-40.0, 0.0)
//            )
//        }


        val anchorPoint = Vector2(.0,-5.0)
        val displacement = 10.0
        val numPins = 10
        for (v in 0.. numPins) {
            val theta = (PI / numPins.toDouble()) * v.toDouble()
            val offset = Vector2(-1.0, 0.0)
            offset.rotate(-theta)
            offset.setMagnitude(displacement)
            val newPos = anchorPoint + offset
            placePinInWorld(world, newPos.x, newPos.y)
        }
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

private operator fun Vector2.plus(other: Vector2): Vector2 {
    return this.sum(other)
}

@Composable
fun PachinkoView(sharedState: SharedState, onEvent: (AnyEvent) -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PachinkoGameView(context)
        }
    )
}

