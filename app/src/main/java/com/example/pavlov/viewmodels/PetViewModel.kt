package com.example.pavlov.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pavlov.models.PetAccessoryTransactionRecord
import com.example.pavlov.models.PetDao
import com.example.pavlov.models.PetEquip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PetViewModel(
    private val petDao: PetDao,
) : ViewModel() {
    private val _state = MutableStateFlow(PetState())
    private val _purchasedAccessories = petDao.getAllPurchasedAccessories()
    private val _activeHat = petDao.getAccessorySlot(PetAccessoryType.HAT)
    private val _activeFace = petDao.getAccessorySlot(PetAccessoryType.FACE)

    val state = combine(
        _state,
        _purchasedAccessories,
        _activeHat,
        _activeFace) { state, purchased, hat, face ->
        Unit
        state.copy(
            equippedHat = hat ?: PetAccessory.NONE,
            purchasedHats = purchased.filter { it.type == PetAccessoryType.HAT },
            equippedFace = face ?: PetAccessory.NONE,
            purchasedFaces = purchased.filter { it.type == PetAccessoryType.FACE },
            shopItems = PetAccessory
                .getUnPurchasedAccessories(purchased)
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PetState())

    fun onEvent(event: PetEvent) {
        when(event) {
            is PetEvent.equipAccessory -> {
                viewModelScope.launch {
                    petDao.equipAccessory(PetEquip(event.type, event.accessory))
                }
            }
            is PetEvent.purchaseAccessory -> {
                viewModelScope.launch {
                    petDao.purchaseAccessory(PetAccessoryTransactionRecord(
                        event.accessory,
                        event.accessory.type
                    ))
                }
            }
        }
    }
}