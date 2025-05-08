package com.example.pavlov.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.pavlov.R
import com.example.pavlov.models.CasinoGame
import com.example.pavlov.theme.CasinoTheme
import com.example.pavlov.PavlovApplication

/**
 * Provides the list of available casino games
 */
@Composable
fun getCasinoGames(): List<CasinoGame> {
    val isDarkMode by PavlovApplication.isDarkTheme.collectAsState()
    val tileGradient = if (isDarkMode) {
        CasinoTheme.BronzeGradient
    } else {
        CasinoTheme.PlatinumGradient
    }

    return listOf(
            CasinoGame(
                name = "Scratcher",
                iconResId = R.drawable.scratcher_icon,
                gradient = tileGradient,
                costInTreats = 5,
            ),
            CasinoGame(
                name = "Roulette",
                iconResId = R.drawable.roulette_icon,
                gradient = tileGradient,
                costInTreats = 8,
            ),
            CasinoGame(
                name = "Slots",
                iconResId = R.drawable.slots_icon,
                gradient = tileGradient,
                costInTreats = 10,
            ),
            CasinoGame(
                name = "Pachinko",
                iconResId = R.drawable.pachinko_icon,
                gradient = tileGradient,
                costInTreats = 15,
            ),
            CasinoGame(
                name = "Poker",
                iconResId = R.drawable.card_icon,
                gradient = tileGradient,
                costInTreats = 12,
            )
        )
    }

/**
 * Displays grid of game tiles
 */
@Composable
fun CasinoGamesList(
    onGameSelected: (CasinoGame) -> Unit,
    modifier: Modifier = Modifier
) {
    val casinoGames = getCasinoGames()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        casinoGames.chunked(2).forEach { rowGames ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowGames.forEach { game ->
                    GameTile(
                        game = game,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onGameSelected(game) }
                    )
                }

                if (rowGames.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}