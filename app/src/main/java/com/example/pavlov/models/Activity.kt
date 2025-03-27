package com.example.pavlov.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Whenever the user completes an activity it is appended to the log with some additional metadata
 * These activities can be associated to their respective goals by the goalId
 */
@Entity(tableName = "activity_log")
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "goal_id") val goalId: Int,
    @ColumnInfo(name = "completion_timestamp") val completionTimestamp: LocalDateTime,
    @ColumnInfo(name = "completion_note") val completionNote: String = "",
)
