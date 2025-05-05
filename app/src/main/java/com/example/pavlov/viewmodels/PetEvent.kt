package com.example.pavlov.viewmodels

sealed interface PetEvent : AnyEvent {
    data class purchaseAccessory(val accessory: PetAccessory) : PetEvent
    data class equipAccessory(val type: PetAccessoryType, val accessory: PetAccessory) : PetEvent
}