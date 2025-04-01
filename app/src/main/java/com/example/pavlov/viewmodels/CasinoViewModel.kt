package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pavlov.PavlovApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CasinoViewModel: ViewModel() {
    // The internal state of the view model is private so that the UI can only
    // send updates to the state through the GoalsEvent Interface
    private val _state = MutableStateFlow(CasinoState())
    // Consumers of the GoalViewModel API subscribe to this StateFlow
    // to receive update to the UI state
    val state = _state.asStateFlow()

    fun onEvent(event: CasinoEvent) {
        when(event) {
            is CasinoEvent.SelectGame -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedGame = event.game,
                        showGameDialog = true
                    )
                }
            }
            is CasinoEvent.CloseGameDialog -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedGame = null,
                        showGameDialog = false,
                        isPlaying = false
                    )
                }
            }

            is CasinoEvent.PlayGame -> {
                _state.update { currentState ->
                    currentState.copy(
                        isPlaying = true
                    )
                }

                // game logic here
                viewModelScope.launch {
                }
            }

            is CasinoEvent.SpendTreats -> {
                // Spend treats to play games
                PavlovApplication.removeTreats(event.amount)
            }
        }
    }
}