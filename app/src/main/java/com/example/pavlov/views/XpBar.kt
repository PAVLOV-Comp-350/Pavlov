package com.example.pavlov.views


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun XpBar(
    currentXp: Int,
    xpToNextLevel: Int,
    modifier: Modifier = Modifier
) {
    val progress = (currentXp.toFloat() / xpToNextLevel).coerceIn(0f, 1f)
    Column(modifier = modifier) {
        Text(
            text = "INSERT LEVEL TITLE",
            fontSize = 19.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }
    }
}
