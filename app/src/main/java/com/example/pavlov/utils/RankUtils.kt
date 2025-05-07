package com.example.pavlov.utils

import kotlin.math.pow

val xpRanks = listOf(
    1 to 0,
    2 to 100,
    3 to 250,
    4 to 400,
    5 to 600,
    6 to 800,
    7 to 1100,
    8 to 1400,
    9 to 1800,
    10 to 2300,
    11 to 2900,
    12 to 3600,
    13 to 4500,
)




val xpTitles = listOf(
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
    val baseXp = 30.0
    val multiplier = 1.1
    return (baseXp * multiplier.pow(level - 1)).toInt()
}

fun getRank(xp: Int): Int {
    return xpRanks.lastOrNull { xp >= it.second }?.first ?: 1
}

fun getCurrentRankStartXp(xp: Int): Int {
    return xpRanks.lastOrNull { xp >= it.second }?.second ?: 0
}

fun getNextRankXP(xp: Int): Int {
    return xpRanks.firstOrNull { xp < it.second }?.second ?: (xp + 1000)
}

fun getTitleForXp(xp: Int): String {
    return xpTitles.lastOrNull { xp >= it.first }?.second ?: "Unknown"
}

