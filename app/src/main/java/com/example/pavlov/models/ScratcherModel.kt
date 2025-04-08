package com.example.pavlov.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * scratcher game instance in the database
 */
@Entity(tableName = "scratchers")
data class Scratcher(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "is_scratched") val isScratched: Boolean = false,
    @ColumnInfo(name = "prize_amount") val prizeAmount: Int = 0,
    @ColumnInfo(name = "creation_timestamp") val creationTimestamp: Long = System.currentTimeMillis()
)

/**
 * state of the scratcher game
 */
data class ScratcherGameState(
    val cells: List<ScratcherCell> = List(9) { ScratcherCell() },
    val isComplete: Boolean = false,
    val totalPrize: Int = 0
)

/**
 * a single cell in the scratcher game
 */
data class ScratcherCell(
    val value: Int = 0,
    val isRevealed: Boolean = false
)