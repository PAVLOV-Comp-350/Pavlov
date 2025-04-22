package com.example.pavlov.games

import android.graphics.Canvas
import com.example.pavlov.utils.ViewMetrics

interface Game<Events> {
    fun setup(metrics: ViewMetrics)
    fun update(deltaTime: Double)
    fun render(canvas: Canvas)
    fun onEvent(event: Events)
    fun onResize(viewMetrics: ViewMetrics)
}
