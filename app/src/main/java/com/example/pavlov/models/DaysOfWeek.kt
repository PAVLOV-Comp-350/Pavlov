package com.example.pavlov.models

/**
 * Days of the week as bit flags for activeDays
 */
object DaysOfWeek {
    const val MONDAY = 1       // 0000001
    const val TUESDAY = 2      // 0000010
    const val WEDNESDAY = 4    // 0000100
    const val THURSDAY = 8     // 0001000
    const val FRIDAY = 16      // 0010000
    const val SATURDAY = 32    // 0100000
    const val SUNDAY = 64      // 1000000
    const val ALL_DAYS = 127   // 1111111

    // Check if a specific day is active
    fun isDayActive(activeDays: Int, day: Int): Boolean {
        return activeDays and day != 0
    }

    // Toggle a day in the activeDays bitfield
    fun toggleDay(activeDays: Int, day: Int): Int {
        return activeDays xor day
    }

    // Set a day as active
    fun setDayActive(activeDays: Int, day: Int): Int {
        return activeDays or day
    }

    // Set a day as inactive
    fun setDayInactive(activeDays: Int, day: Int): Int {
        return activeDays and day.inv()
    }
}