package com.example.pavlov.models

import android.content.Context
import android.media.SoundPool
import com.example.pavlov.R

object SoundManager{
    private lateinit var soundPool: SoundPool
    private var rouletteSoundId: Int = 0
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        soundPool = SoundPool.Builder().setMaxStreams(5).build()
        rouletteSoundId = soundPool.load(context, R.raw.item_roulette, 1)
        isInitialized = true
    }

    fun playRouletteSound( ){
        if (isInitialized) {
            soundPool.play(
                rouletteSoundId,
                1f,1f,0,0,1f
            )
        }
    }

    fun release() {
        soundPool.release()
        isInitialized = false
    }
}
