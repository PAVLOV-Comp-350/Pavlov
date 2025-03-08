package com.example.pavlov.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pavlov.viewmodels.GoalsEvent

/**
 * Added this separate file for the @Composable used for the alert dialog
 * **/
@Composable
fun GoalAddPopup(
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String, Int) -> Unit,
) {
    //Allocates the values which will be used to populate the new goal.
    val id: Int = 0
    var goalTitle by remember { mutableStateOf("") }
    var goalDescription by remember { mutableStateOf("") }
    val streak: Int = 0

    //used an AlertDialog as it comes with pre-made buttons
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add a Goal") },
        text = {
            Column {
                OutlinedTextField(
                    value = goalTitle,
                    onValueChange = { goalTitle = it },
                    label = { Text("Goal Title") }
                )

                OutlinedTextField(
                    value = goalDescription,
                    onValueChange = { goalDescription = it },
                    label = { Text("Goal Description") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (goalTitle.isNotBlank() && goalDescription.isNotBlank()) {
                    onConfirm(id, goalTitle, goalDescription, streak)
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