package com.example.pavlov.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import kotlin.time.Duration

/**
 * Data class for the Goal model
 * NOTE(Devin): The defaultValue annotations are only for the newer fields that may not
 * necessarily exist in older schemas.
 */
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    /** Name of the Goal */
    val title: String,
    val description: String,
    /** Current count in days that a goal has been upheld */
    val streak: Int,
    /** How often the goal resets */
    @ColumnInfo(defaultValue = "0") val frequency: GoalFrequency,
    /** Is this a simple Yes/No goal*/
    @ColumnInfo(defaultValue = "FALSE") val simple: Boolean,
    /** The metric for tracking progress towards the goal e.g. minute, cups of water, etc... */
    @ColumnInfo(defaultValue = "No Unit") val unit: String,
    /** The current amount of the unit */
    @ColumnInfo(defaultValue = "0") val current: Int,
    /** The target amount of the unit that satisfies the goal */
    @ColumnInfo(defaultValue = "0") val target: Int,
    /** Days of the week goal is active */
    @ColumnInfo(defaultValue = "127") val activeDays: Int = 127, // Default to all days (1111111 in binary)
    /** Scheduled time for the task */
    @ColumnInfo(defaultValue = "540") val scheduledTimeMinutes: Int = 540,
)

/**
 * All Goals have an associated frequency.
 * This tell us the span of time that a goal is tracked over.
 * DAILY, WEEKLY, and MONTHLY are special as they also imply the start and end date of the goal.
 * TODO: Create a scheme for CUSTOM
 */
enum class GoalFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}
