package com.example.pavlov.viewmodels

import com.example.pavlov.views.Screen

data class SharedState(
    val activeScreen: Screen = Screen.Goals,
    val treats: Int = 0,
)
