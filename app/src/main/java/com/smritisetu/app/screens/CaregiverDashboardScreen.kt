package com.smritisetu.app.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.smritisetu.app.data.AlertsRepository
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton

@Composable
fun CaregiverDashboardScreen(navController: NavController) {
    val weeklyScores = listOf(60, 65, 58, 70, 75, 80, 78)

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.caregiverColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "👨‍👩‍👧 Caregiver Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
                Text("Patient: Anil Baruah, Age 74", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(20.dp))

                // Quick nav cards to new detailed screens
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    DashboardNavCard("👤", "Patient\nProfile", Modifier.weight(1f)) {
                        navController.navigate("patient_profile")
                    }
                    DashboardNavCard("📊", "Monitoring &\nAnalytics", Modifier.weight(1f)) {
                        navController.navigate("monitoring")
                    }
                    DashboardNavCard(
                        "🔔",
                        "Alerts\n(${AlertsRepository.totalUnreadCount} new)",
                        Modifier.weight(1f),
                        highlight = AlertsRepository.unreadHighPriorityCount > 0
                    ) {
                        navController.navigate("alerts")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Action Cards
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    DashboardNavCard(
                        "📄", 
                        "Weekly\nReport", 
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigate("weekly_report")
                    }
                    
                    DashboardNavCard(
                        "📣", 
                        "Send\nAlert", 
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigate("send_notification")
                    }
                }

                Spacer(Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Weekly Cognitive Performance", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
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
                        Spacer(Modifier.height(12.dp))
                        SmritiButton(
                            text = "View All Alerts →",
                            onClick = { navController.navigate("alerts") },
                            containerColor = Color(0xFFC62828),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DashboardNavCard(
    icon: String,
    label: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = (if (highlight) Color(0xFFFFEBEE) else Color.White).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatCard(icon: String, label: String, value: String) {
    Card(
        modifier = Modifier
            .height(110.dp)
            .width(165.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, style = MaterialTheme.typography.headlineSmall)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun BarChart(data: List<Int>) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)) {
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
