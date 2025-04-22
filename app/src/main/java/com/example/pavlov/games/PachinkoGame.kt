package com.example.pavlov.games

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.util.Size
import androidx.core.graphics.withMatrix
import com.example.pavlov.PavlovApplication
import com.example.pavlov.R
import com.example.pavlov.utils.Vec2
import com.example.pavlov.utils.ViewMetrics
import com.example.pavlov.viewmodels.PachinkoUiEvent
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Circle
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.MassType
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World
import kotlin.math.PI
import kotlin.random.Random

class PachinkoGame : Game<PachinkoUiEvent> {
    private var worldToCanvas = Matrix()
    private var canvasToWorld = Matrix()

    // TODO: Constrain world bounds
    private var world = World<PachinkoBody>()

    private lateinit var steelBall: Bitmap
    private lateinit var goldenPeg: Bitmap
    private lateinit var viewMetrics: ViewMetrics

    override fun setup(metrics: ViewMetrics) {
        viewMetrics = metrics
        updateTransformationMatrices(viewMetrics.size, viewMetrics.density)
        val resources = PavlovApplication.instance.applicationContext.resources
        steelBall = BitmapFactory.decodeResource(resources, R.drawable.steel_ball)
        goldenPeg = BitmapFactory.decodeResource(resources, R.drawable.gold_peg)
        initializeGameWorld(viewMetrics)
    }

    override fun update(deltaTime: Double) {
        world.update(deltaTime)
    }

    override fun render(canvas: Canvas) {
        with(canvas) {
            withMatrix(worldToCanvas) {
                for (body in world.bodies) {
                    body.render(canvas)
                }
            }
        }
    }

    override fun onResize(viewMetrics: ViewMetrics) {
        updateTransformationMatrices(viewMetrics.size, viewMetrics.density)
    }

    override fun onEvent(event: PachinkoUiEvent) {
        when (event) {
            is PachinkoUiEvent.LaunchBall -> {
                val b = PachinkoBall(steelBall)
                val launchPower = 50000.0
                b.setLinearVelocity(.0, launchPower * event.power)
                world.addBody(b)
            }
            PachinkoUiEvent.RegenerateGameBoard -> {
                // Reset the world
                world = World<PachinkoBody>()
                initializeGameWorld(viewMetrics)
            }
        }
    }

    private fun updateTransformationMatrices(size: Size, density: Float) {
        val scale = GameEngine.PIXELS_PER_METER * density
        worldToCanvas.reset()
        worldToCanvas.postScale(scale, -scale)
        worldToCanvas.postTranslate(size.width * 0.5f, size.height * 0.5f)
        if (!worldToCanvas.invert(canvasToWorld)) {
            Log.e("Matrix Inversion Failed", "")
        }
    }

    private fun initializeGameWorld(viewMetrics: ViewMetrics) {
        world.gravity.set(Vector2(0.0, -400.0))


        // Generate random heights for root points
        var rootPoints = mutableListOf<Vector2>()
        val numRootPoints = Random.nextInt(6, 10)
        val climbMin = 6.5
        val climbMax = 8.5
        val spreadMin = 6.82
        val spreadMax = 16.0
        val startHeight = -40.0
        var spawnHeight = startHeight + Random.nextDouble(climbMin, climbMax)
        for (v in 0..<numRootPoints) {
            val offset = Random.nextDouble(spreadMin, spreadMax)
            rootPoints += Vector2(offset, spawnHeight)
            rootPoints += Vector2(-offset, spawnHeight)
            spawnHeight += Random.nextDouble(climbMin, climbMax)
        }

        for (p in rootPoints) {
            world.addBody(DebugMarker(p))
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


}
sealed class PachinkoBody : Body() {
    abstract fun render(canvas: Canvas)
}

class PachinkoBall(
    val res: Bitmap, pos: Vector2 = Vector2(.0, .0)
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
    val res: Bitmap, pos: Vector2 = Vector2(.0, .0)
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
    pos: Vector2,
    val color: Int = Color.RED,
    private val debugRadius: Float = 0.8f,
) : PachinkoBody() {
    init {
        translate(pos)
    }

    override fun render(canvas: Canvas) {
        val pos = transform.translation.toVec2()
        val paint = Paint()
        paint.color = color
        canvas.drawCircle(pos.x, pos.y, debugRadius, paint)
    }

}

private fun Canvas.drawBitmapCentered(
    bmp: Bitmap, pos: Vec2, size: Vec2, src: Rect? = null, paint: Paint? = null
) {
    drawBitmap(
        bmp, src, RectF(
            pos.x - size.x,
            pos.y - size.y,
            pos.x + size.x,
            pos.y + size.y,
        ), paint
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