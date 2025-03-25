package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pavlov.PavlovApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SharedViewModel: ViewModel() {
    // The internal state of the view model is private so that the UI can only
    // send updates to the state through the GoalsEvent Interface
    private val _state = MutableStateFlow(SharedState())
    // Consumers of the GoalViewModel API subscribe to this StateFlow
    // to receive update to the UI state
    val state = combine(_state, PavlovApplication.treats) { state, treats ->
        Unit
        state.copy(
            treats = treats
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SharedState())

    fun onEvent(event: SharedEvent) {
        when(event) {
            is SharedEvent.SetScreen -> {
                _state.value = _state.value.copy(
                    activeScreen = event.screen
                )
            }
        }
    }
}