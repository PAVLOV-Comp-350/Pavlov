package com.example.pavlov.viewmodels

import com.example.pavlov.views.Screen

sealed interface SharedEvent {
    data class SetScreen(val screen: Screen) : SharedEvent
}