package com.example.pavlov.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pavlov.viewmodels.PetAccessory
import com.example.pavlov.viewmodels.PetAccessoryType

@Entity(tableName = "purchase_log")
data class PetAccessoryTransactionRecord (
    @PrimaryKey()
    @ColumnInfo(name = "item_type", defaultValue = "0")
    val itemType: PetAccessory = PetAccessory.NONE,
    @ColumnInfo(name = "slot_type")
    val slotType: PetAccessoryType,
)