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
import com.example.pavlov.models.DaysOfWeek

/**
 * A component that displays selectable day buttons for the days of the week
 */
@Composable
fun DaySelectionRow(
    activeDays: Int,
    onDayToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DayButton("M", DaysOfWeek.MONDAY, activeDays, onDayToggle)
        DayButton("T", DaysOfWeek.TUESDAY, activeDays, onDayToggle)
        DayButton("W", DaysOfWeek.WEDNESDAY, activeDays, onDayToggle)
        DayButton("TH", DaysOfWeek.THURSDAY, activeDays, onDayToggle)
        DayButton("F", DaysOfWeek.FRIDAY, activeDays, onDayToggle)
        DayButton("S", DaysOfWeek.SATURDAY, activeDays, onDayToggle)
        DayButton("SU", DaysOfWeek.SUNDAY, activeDays, onDayToggle)
    }
}

@Composable
fun DayButton(
    dayLabel: String,
    dayFlag: Int,
    activeDays: Int,
    onDayToggle: (Int) -> Unit
) {
    val isActive = DaysOfWeek.isDayActive(activeDays, dayFlag)

    Surface(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape),
        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        onClick = { onDayToggle(dayFlag) }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = dayLabel,
                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}