package com.example.pavlov.models

import java.time.DayOfWeek
import java.time.LocalDateTime

enum class PavlovDayOfWeek(val bit: Int, val abbrev: String) {
    MONDAY(0, "M"),
    TUESDAY(1, "T"),
    WEDNESDAY(2, "W"),
    THURSDAY(3, "Th"),
    FRIDAY(4, "F"),
    SATURDAY(5, "Sa"),
    SUNDAY(6, "Su");

    companion object {
        fun fromNative(native: DayOfWeek): PavlovDayOfWeek {
            return when(native) {
                DayOfWeek.MONDAY -> MONDAY
                DayOfWeek.TUESDAY -> TUESDAY
                DayOfWeek.WEDNESDAY -> WEDNESDAY
                DayOfWeek.THURSDAY -> THURSDAY
                DayOfWeek.FRIDAY -> FRIDAY
                DayOfWeek.SATURDAY -> SATURDAY
                DayOfWeek.SUNDAY -> SUNDAY
            }
        }
        fun today(): PavlovDayOfWeek {
            return PavlovDayOfWeek.fromNative(LocalDateTime.now().dayOfWeek)
        }
    }

}



