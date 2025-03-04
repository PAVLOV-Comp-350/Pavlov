package com.example.pavlov.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class for the Goal model
 * NOTE(Devin): the @ColumnInfo(name = ...) is because SQL database names are case insensitive, so
 * isCompleted maps to iscompleted. This is why we change the name to snake_case
 */
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String = "",
    val description: String = "",
    val streak: Int,
    // QUESTION(Devin): Should we record some interval for the streak to reset per goal?
    // e.g. val streakResetInterval: Duration
)
