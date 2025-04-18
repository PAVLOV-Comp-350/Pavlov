package com.example.pavlov.utils

import kotlin.math.pow


val xpRanks = listOf(
    0 to "Worm",
    100 to "Beggar",
    250 to "Potato",
    400 to "Failure",
    600 to "Barley Functioning Human",
    800 to "Almost Competent",
    1100 to "Not Totally Useless",
    1400 to "Occasionally Impressive",
    1800 to "Overachiever",
    2300 to "Productivity Machine",
    2900 to "Frighteningly Efficient",
    3600 to "Scrum Master",
    4500 to "Scrum Lord Supreme"
)


fun getXpReward(level: Int): Int {
    val baseXp = 50.0
    val multiplier = 1.1
    return (baseXp * multiplier.pow(level - 1)).toInt()
}

fun getRank(xp: Int): String {
    return xpRanks.lastOrNull { xp >= it.first }?.second ?: "Unknown"
}


fun getCurrentRankStartXp(xp: Int): Int {
    return xpRanks.lastOrNull { xp >= it.first }?.first ?: 0
}

fun getNextRankXP(xp: Int): Int {
    return xpRanks.firstOrNull { xp < it.first }?.first ?: (xp + 1000)
}
