package com.example.pavlov.models

data class CardGameState(
    val deck: List<String> = listOf(
        "AS" + "AH" + "AC" + "AD" +
        "KS" + "KH" + "kC" + "KD" +
        "QS" + "QH" + "QC" + "QD" +
        "JS" + "JH" + "JC" + "JD" +
        "10S" + "10H" + "10C" + "10D" +
        "9S" + "9H" + "9C" + "9D" +
        "8S" + "8H" + "8C" + "8D" +
        "7S" + "7H" + "7C" + "7D" +
        "6S" + "6H" + "6C" + "6D" +
        "5S" + "5H" + "5C" + "5D" +
        "4S" + "4H" + "4C" + "4D" +
        "3S" + "3H" + "3C" + "3D" +
        "2S" + "2H" + "2C" + "2D"),
    val totalPrize: Int = 0,
)