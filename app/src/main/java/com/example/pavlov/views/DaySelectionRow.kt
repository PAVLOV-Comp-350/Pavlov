package com.example.pavlov.views


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pavlov.models.PavlovDayOfWeek
import com.example.pavlov.models.PavlovDaysOfWeek

/**
 * A component that displays selectable day buttons for the days of the week
 */
@Composable
fun DaySelectionRow(
    activeDays: PavlovDaysOfWeek,
    onDayToggle: (PavlovDayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PavlovDayOfWeek.entries.forEach{
            DayButton(it, activeDays, onDayToggle)
        }
    }
}

@Composable
fun DayButton(
    day: PavlovDayOfWeek,
    activeDays: PavlovDaysOfWeek,
    onDayToggle: (PavlovDayOfWeek) -> Unit
) {
    val isActive = activeDays.isDayActive(day)
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape),
        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        onClick = { onDayToggle(day) }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = day.abbrev,
                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}