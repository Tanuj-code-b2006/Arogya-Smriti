package com.smritisetu.app.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen

@Composable
fun CaregiverDashboardScreen(navController: NavController) {
    val weeklyScores = listOf(60, 65, 58, 70, 75, 80, 78) // mock accuracy % per day

    AnimatedScreen {
        AnimatedGradientBox {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    "👨‍👩‍👧 Caregiver Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Patient: Anil Baruah, Age 74", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Weekly Cognitive Performance",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        BarChart(weeklyScores)
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("🎮", "Sessions this week", "12")
                    StatCard("⏰", "Reminders missed", "2")
                }

                Spacer(Modifier.height(20.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE).copy(alpha = 0.9f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("⚠️ AI Alert", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "Slight drop in attention-game accuracy detected on Wednesday. Consider a check-in.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Back")
                }
            }
        }
    }
}

@Composable
fun StatCard(icon: String, label: String, value: String) {
    Card(modifier = Modifier.height(100.dp).width(160.dp), shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, style = MaterialTheme.typography.headlineSmall)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun BarChart(data: List<Int>) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
        val barWidth = size.width / (data.size * 2)
        val maxVal = 100f
        data.forEachIndexed { i, value ->
            val barHeight = (value / maxVal) * size.height
            val x = i * (barWidth * 2) + barWidth / 2
            drawRect(
                color = Color(0xFF2E7D32),
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth, barHeight)
            )
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        days.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
    }
}
