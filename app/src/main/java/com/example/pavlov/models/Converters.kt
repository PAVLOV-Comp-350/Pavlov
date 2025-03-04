package com.example.pavlov.models

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * This Is where we serialize and deserialize more complex datatypes like Timestamps into simpler
 * data primitives like Int, String, ... so that we can store them in SQLite.
 *
 * Refer to: https://developer.android.com/training/data-storage/room/referencing-data
 */
class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }
}