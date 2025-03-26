package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pavlov.PavlovApplication
import com.example.pavlov.models.Activity
import com.example.pavlov.models.DaysOfWeek
import com.example.pavlov.models.Goal
import com.example.pavlov.models.GoalFrequency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CasinoViewModel: ViewModel() {
    // The internal state of the view model is private so that the UI can only
    // send updates to the state through the GoalsEvent Interface
    private val _state = MutableStateFlow(CasinoState())
    // Consumers of the GoalViewModel API subscribe to this StateFlow
    // to receive update to the UI state
    val state = _state.asStateFlow()

    fun onEvent(event: CasinoEvent) {
        when(event) {
            else -> {}
        }
    }
}