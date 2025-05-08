package com.example.pavlov.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pavlov.viewmodels.PetAccessory
import com.example.pavlov.viewmodels.PetAccessoryType

@Entity(tableName = "pet_equipment")
data class PetEquip(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "slot_type")
    val slotType: PetAccessoryType,
    @ColumnInfo(name = "item_type", defaultValue = "0")
    val itemType: PetAccessory = PetAccessory.NONE,
)

