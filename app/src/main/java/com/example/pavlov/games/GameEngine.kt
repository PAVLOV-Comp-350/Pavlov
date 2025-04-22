package com.example.pavlov.games

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import com.example.pavlov.utils.ViewMetrics
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration.Companion.milliseconds

class GameEngine<Events>(
    private val game: Game<Events>,
    private val events: Flow<Events>,
) {
    private var viewMetricsReady = CountDownLatch(1)
    private var viewMetrics: ViewMetrics? = null
    private var gameThread = GameThread()
    private lateinit var gameCoroutineScope: CoroutineScope
    private var surfaceHolder: SurfaceHolder? = null

    init {
        gameThread.start()
    }

    inner class GameThread : Thread() {
        private lateinit var looper: Looper
        private lateinit var handler: Handler
        private lateinit var gameDispatcher: CoroutineDispatcher

        override fun run() {
            // 1. Prepare the Looper for this thread
            Looper.prepare()
            looper = Looper.myLooper()!!
            // 2. Create the Handler associated with this Looper
            handler = Handler(looper)
            // 3. Create the CoroutineDispatcher from the Handler
            // This dispatcher will post coroutine tasks to the Handler's message queue
            gameDispatcher = handler.asCoroutineDispatcher("GameThreadDispatcher")
            // 4. Create the game's coroutine scope
            gameCoroutineScope = CoroutineScope(gameDispatcher + SupervisorJob())

            // 5. Run the Game's setup function
            viewMetricsReady.await()
            game.setup(viewMetrics!!)

            // Event Collector Coroutine
            gameCoroutineScope.launch {
                events.collect { game.onEvent(it) }
            }
            // Main Game Loop Coroutine
            gameCoroutineScope.launch {
                var lastFrameTime = System.nanoTime()
                while (isActive) {
                    val currentTime = System.nanoTime()
                    val deltaTimeInSeconds = (currentTime - lastFrameTime) / 1_000_000_000.0
                    game.update(deltaTimeInSeconds)
                    val canvas = surfaceHolder?.lockCanvas()
                    if (canvas != null) {
                        canvas.drawColor(Color.BLACK)
                        game.render(canvas)
                        surfaceHolder?.unlockCanvasAndPost(canvas)
                    }
                    lastFrameTime = currentTime
                    delay(TARGET_FRAME_TIME)
                }
            }
            // 6. Start the Looper - this makes the thread process messages
            // The loop() call blocks until Looper.quit() or quitSafely() is called
            Looper.loop()

            Log.e("GameThread", "GameThread loop finished")
        }

        fun quitSafely() {
            handler.post {
                looper.quitSafely()
            }
        }
    }

    fun attachSurfaceHolder(holder: SurfaceHolder, metrics: ViewMetrics) {
        surfaceHolder = holder
        viewMetrics = metrics
        viewMetricsReady.countDown()
    }

    fun detachSurfaceHolder() {
        surfaceHolder = null
    }

    fun updateSurface(metrics: ViewMetrics) {
        viewMetrics = metrics
        game.onResize(metrics)
    }

    fun shutdown() {
        gameCoroutineScope.cancel()
        gameThread.quitSafely()
        var retry = true
        while (retry) {
            try {
                gameThread.join()
                retry = false
            } catch (e: InterruptedException) {
                // Retry
            }
        }
    }

    companion object {
        const val PIXELS_PER_METER = 10
        val TARGET_FRAME_TIME = 16.milliseconds // Target ~60 FPS
    }

}