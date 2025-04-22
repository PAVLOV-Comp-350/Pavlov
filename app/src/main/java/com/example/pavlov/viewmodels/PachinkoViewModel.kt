package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pavlov.games.GameEngine
import com.example.pavlov.games.PachinkoGame
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PachinkoViewModel : ViewModel() {
    private val _uiEventChannel = Channel<PachinkoUiEvent>()
    val uiEventChannel = _uiEventChannel.receiveAsFlow()
    val gameEngine = GameEngine(PachinkoGame(), uiEventChannel)

    fun sendEvent(event: PachinkoUiEvent) {
        viewModelScope.launch {
            _uiEventChannel.send(event)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _uiEventChannel.close()
        gameEngine.shutdown()
    }
}