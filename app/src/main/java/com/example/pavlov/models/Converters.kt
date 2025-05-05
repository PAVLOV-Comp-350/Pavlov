package com.example.pavlov.models

import androidx.room.TypeConverter
import com.example.pavlov.viewmodels.PetAccessory
import com.example.pavlov.viewmodels.PetAccessoryType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration

/**
 * This Is where we serialize and deserialize more complex datatypes like Timestamps into simpler
 * data primitives like Int, String, ... so that we can store them in SQLite.
 *
 * Refer to: https://developer.android.com/training/data-storage/room/referencing-data
 */
class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter fun PetAccessoryToInt(value: PetAccessory?): Int? {
        return value?.ordinal
    }

    @TypeConverter fun IntToPetAccessory(value: Int?): PetAccessory? {
        if (value == null) return null
        return PetAccessory.entries[value]
    }

    @TypeConverter fun PetAccessoryTypeToInt(value: PetAccessoryType?): Int? {
        return value?.ordinal
    }

    @TypeConverter fun IntToPetAccessoryType(value: Int?): PetAccessoryType? {
        if (value == null) return null
        return PetAccessoryType.entries[value]
    }

    @TypeConverter fun TimestamptoDate(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }

    @TypeConverter fun durationToIsoString(duration: Duration?): String? {
        return duration?.toIsoString()
    }

    @TypeConverter fun IsoStringToDuration(value: String?): Duration? {
        val v = value ?: "Invalid"
        return Duration.parseIsoStringOrNull(v)
    }

    @TypeConverter fun GoalFrequencyToInt(frequency: GoalFrequency?): Int? {
        return frequency?.ordinal
    }

    @TypeConverter fun IntToGoalFrequency(v: Int?): GoalFrequency? {
        if (v == null) return null
        return GoalFrequency.entries[v]
    }

    @TypeConverter fun PavlovDaysOfWeekToInt(days: PavlovDaysOfWeek?): Int? {
        return days?.toInt()
    }

    @TypeConverter fun IntToPavlovDaysOfWeek(v: Int?): PavlovDaysOfWeek? {
        if (v == null) return null
        return PavlovDaysOfWeek(v)
    }
}