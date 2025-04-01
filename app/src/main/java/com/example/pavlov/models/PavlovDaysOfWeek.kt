package com.example.pavlov.models

/**
 * Days of the week as bit flags for activeDays
 */
data class PavlovDaysOfWeek(private var internal: Int) {

    private fun dayMask(day: PavlovDayOfWeek): Int {
        return 1 shl day.bit
    }

    // Check if a specific day is active
    fun isDayActive(day: PavlovDayOfWeek): Boolean {
        return internal and dayMask(day) != 0
    }

    // Toggle a day in the activeDays bitfield
    fun toggleDay(day: PavlovDayOfWeek): PavlovDaysOfWeek {
        return PavlovDaysOfWeek(internal xor dayMask(day))
    }

    // Set a day as active
    fun setDay(day: PavlovDayOfWeek, value: Boolean): PavlovDaysOfWeek {
        return PavlovDaysOfWeek(if (value) {
            internal or dayMask(day)
        } else {
            internal and dayMask(day).inv()
        })
    }

    fun toInt(): Int {
        return internal;
    }

    companion object {
        val ALL_DAYS = PavlovDaysOfWeek(127)
    }

}

