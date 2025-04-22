package com.example.pavlov.games

import android.content.Context
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.pavlov.utils.ViewMetrics
import com.example.pavlov.viewmodels.PachinkoViewModel

// TODO: Abstract GameView
class PachinkoGameView(
    context: Context,
    private val vm: PachinkoViewModel,
) : SurfaceView(context), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val density = context.resources.displayMetrics.density
        vm.gameEngine.attachSurfaceHolder(holder, ViewMetrics(Size(width, height), density))
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        val density = context.resources.displayMetrics.density
        vm.gameEngine.updateSurface(ViewMetrics(Size(width, height), density))
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        vm.gameEngine.detachSurfaceHolder()
    }
}

