package com.example.pavlov.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.window.DialogProperties
import com.example.pavlov.viewmodels.GoalsEvent
import com.example.pavlov.viewmodels.GoalsState

/**
 * Added this separate file for the @Composable used for the alert dialog
 * **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalAddPopup(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    state: GoalsState,
    onEvent: (GoalsEvent) -> Unit
) {
    if (state.showPopup) {

        //used an AlertDialog as it comes with pre-made buttons
        AlertDialog(
            onDismissRequest = { onEvent(GoalsEvent.HideAddGoalAlert) },
            title = { Text(text = if (state.isEditMode) "Edit Goal" else "Add Goal") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = state.newGoal.title,
                        onValueChange = { onEvent(GoalsEvent.SetGoalTitle(it)) },
                        label = { Text("Goal Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = state.newGoal.description,
                        onValueChange = { onEvent(GoalsEvent.SetGoalDescription(it)) },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    //Day Selection
                    Text(
                        text = "Active Days",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    DaySelectionRow(
                        activeDays = state.newGoal.activeDays,
                        onDayToggle = { onEvent(GoalsEvent.ToggleGoalDay(it)) },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    //Time select
                    Text(
                        text = "Schedule Time",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    TimeSelector(
                        scheduledTimeMinutes = state.newGoal.scheduledTimeMinutes,
                        onTimeClick = { onEvent(GoalsEvent.ShowTimePicker) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    //Show delete when editing
                    if (state.isEditMode) {
                        Button(
                            onClick = {
                                onEvent(GoalsEvent.DeleteGoal(state.newGoal.id))
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete goal",
                                modifier =  Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Goal")
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onEvent(GoalsEvent.ConfirmAddGoal) }
                ) {
                    Text(text = if (state.isEditMode) "Update" else "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(GoalsEvent.HideAddGoalAlert) }
                ) {
                    Text("Cancel")
                }
            },
        )

        if (state.showTimePickerDialog) {
            TimePickerDialog(
                initialMinutes = state.newGoal.scheduledTimeMinutes,
                onTimeSelected = { minutes ->
                    onEvent(GoalsEvent.SetScheduledTime(minutes))
                },
                onDismiss = { onEvent(GoalsEvent.HideTimePicker) }
            )
        }
    }
}