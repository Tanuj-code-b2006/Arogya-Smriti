package com.smritisetu.app.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppGradients {
    val mainBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF8E1), // Warm Cream
            Color(0xFFFFE0B2)  // Soft Orange
        )
    )

    val patientButton = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFEF6C00), // Orange
            Color(0xFFFF9800)  // Light Orange
        )
    )

    val caregiverButton = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF2E7D32), // Green
            Color(0xFF4CAF50)  // Light Green
        )
    )
}