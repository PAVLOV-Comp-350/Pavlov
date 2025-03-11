package com.example.pavlov.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.pavlov.viewmodels.GoalsEvent
import com.example.pavlov.viewmodels.GoalsState

/**
 * Added this separate file for the @Composable used for the alert dialog
 * **/
@Composable
fun GoalAddPopup(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    state: GoalsState,
    onEvent: (GoalsEvent) -> Unit
) {

    //used an AlertDialog as it comes with pre-made buttons
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add a Goal") },
        text = {
            Column {
                OutlinedTextField(
                    value = state.newGoalTitle,
                    onValueChange = { onEvent(GoalsEvent.SetGoalTitle(it)) },
                    label = { Text("Goal Title") }
                )

                OutlinedTextField(
                    value = state.newGoalDescription,
                    onValueChange = { onEvent(GoalsEvent.SetGoalDescription(it)) },
                    label = { Text("Goal Description") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (state.newGoalTitle.isNotBlank() && state.newGoalDescription.isNotBlank()) {
                    onConfirm()
                }
            }) {
                Text("Add Goal")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}