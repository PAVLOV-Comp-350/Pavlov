package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PachinkoViewModel : ViewModel() {
    private val _uiEventChannel = Channel<PachinkoUiEvent>()
    val uiEventChannel = _uiEventChannel.receiveAsFlow()

    fun sendEvent(event: PachinkoUiEvent) {
        viewModelScope.launch {
            _uiEventChannel.send(event)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _uiEventChannel.close()
    }
}