package com.example.pavlov.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlin.math.max
import kotlin.math.min

/**
 * A time picker component that allows users to select hours and minutes
 */
@Composable
fun TimePickerDialog(
    initialMinutes: Int,
    onTimeSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    // Convert minutes to hours and minutes
    val initialHours24 = initialMinutes / 60
    val initialMins = initialMinutes % 60

    val initialHours12 = when {
        initialHours24 == 0 -> 12
        initialHours24 > 12 -> initialHours24 - 12
        else -> initialHours24
    }

    val initialIsAM = initialHours24 < 12

    var hours12 by remember { mutableIntStateOf(initialHours12) }
    var minutes by remember { mutableIntStateOf(initialMins) }
    var isAM by remember { mutableStateOf(initialIsAM)}

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set Time",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Time display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hours
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = {
                                hours12 = when {
                                    hours12 == 12 -> 1
                                    else -> hours12 + 1
                                }
                            }
                        ) {
                            Text("▲", style = MaterialTheme.typography.titleLarge)
                        }

                        Text(
                            text = String.format("%02d", hours12),
                            style = MaterialTheme.typography.displayMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(80.dp)
                        )

                        IconButton(
                            onClick = {
                                hours12 = when {
                                    hours12 == 1 -> 12
                                    else -> hours12 - 1
                                }

                            }
                        ) {
                            Text("▼", style = MaterialTheme.typography.titleLarge)
                        }
                    }

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Minutes
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { minutes = min(59, minutes + 5) }
                        ) {
                            Text("▲", style = MaterialTheme.typography.titleLarge)
                        }

                        Text(
                            text = String.format("%02d", minutes),
                            style = MaterialTheme.typography.displayMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(80.dp)
                        )

                        IconButton(
                            onClick = { minutes = max(0, minutes - 5) }
                        ) {
                            Text("▼", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilterChip(
                        selected = isAM,
                        onClick = { isAM = true },
                        label = { Text("AM") }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    FilterChip(
                        selected = !isAM,
                        onClick = { isAM = false },
                        label = { Text("PM") }
                    )
                }

                // Format time
                val period = if (isAM) "AM" else "PM"
                Text(
                    text = String.format("%d:%02d %s", hours12, minutes, period),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {

                            val hours24 = when{
                                isAM && hours12 == 12 -> 0
                                !isAM && hours12 == 12 -> 12 // 12 PM ->12
                                !isAM -> hours12 + 12 //PM
                                else ->hours12 // AM
                            }

                            val totalMinutes = hours24 * 60 + minutes
                            onTimeSelected(totalMinutes)
                        }
                    ) {
                        Text("Set")
                    }
                }
            }
        }
    }
}

/**
 * displays the selected time and opens the time picker
 */
@Composable
fun TimeSelector(
    scheduledTimeMinutes: Int,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hours24 = scheduledTimeMinutes / 60
    val minutes = scheduledTimeMinutes % 60

    val displayHours = when {
        hours24 == 0 -> 12 // 12 AM
        hours24 > 12 -> hours24 - 12 // PM hours
        else -> hours24 // AM hours
    }

    val period = if (hours24 < 12) "AM" else "PM"
    val timeString = String.format("%d:%02d %s", displayHours, minutes, period)

    Button(
        onClick = onTimeClick,
        modifier = modifier
    ) {
        Text("Time: $timeString")
    }
}