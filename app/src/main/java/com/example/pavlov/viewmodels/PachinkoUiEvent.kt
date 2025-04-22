package com.example.pavlov.viewmodels

sealed interface PachinkoUiEvent {
    data class LaunchBall(val power: Float) : PachinkoUiEvent
}