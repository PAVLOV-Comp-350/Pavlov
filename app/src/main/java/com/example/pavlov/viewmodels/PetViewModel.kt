package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PetViewModel : ViewModel() {
    // The internal state of the view model is private so that the UI can only
    // send updates to the state through the GoalsEvent Interface
    private val _state = MutableStateFlow(PetState())

    // Consumers of the GoalViewModel API subscribe to this StateFlow
    // to receive update to the UI state
    val state = _state.asStateFlow()

    fun onEvent(event: PetEvent) {

    }
}