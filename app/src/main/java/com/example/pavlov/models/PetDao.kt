package com.example.pavlov.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.pavlov.viewmodels.PetAccessory
import com.example.pavlov.viewmodels.PetAccessoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Upsert
    suspend fun equipAccessory(slot: PetEquip)
    @Query("SELECT item_type FROM pet_equipment WHERE slot_type = :slot LIMIT 1")
    fun getAccessorySlot(slot: PetAccessoryType): Flow<PetAccessory?>
    @Insert
    suspend fun purchaseAccessory(transaction: PetAccessoryTransactionRecord)
    @Query("SELECT item_type FROM purchase_log")
    fun getAllPurchasedAccessories(): Flow<List<PetAccessory>>
}