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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients

data class GameSession(val date: String, val game: String, val accuracy: Int, val duration: String)

@Composable
fun MonitoringScreen(navController: NavController) {
    val sessions = listOf(
        GameSession("Sep 7", "Memory Match", 82, "12 min"),
        GameSession("Sep 6", "AI Chat Companion", 90, "8 min"),
        GameSession("Sep 6", "Attention Tap", 65, "6 min"),
        GameSession("Sep 5", "Routine Sequencer", 75, "9 min"),
        GameSession("Sep 4", "Memory Match", 70, "11 min"),
        GameSession("Sep 3", "Attention Tap", 58, "7 min"),
    )
    val domainScores = mapOf(
        "Memory" to 78, "Attention" to 62, "Routine Recall" to 75, "Pattern Recognition" to 68
    )

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
                        "📊 Cognitive Monitoring",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
                Spacer(Modifier.height(16.dp))

                // Trend line chart
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("30-Day Accuracy Trend", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        LineTrendChart(listOf(55, 58, 60, 57, 63, 68, 65, 70, 72, 75, 71, 78, 80, 82))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Trend: ↗ Improving overall (last 2 weeks)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Domain-wise breakdown
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Domain-wise Performance", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        domainScores.forEach { (domain, score) ->
                            DomainProgressBar(domain, score)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Engagement stats row
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    MonitoringStatCard("🔥", "Day Streak", "9")
                    MonitoringStatCard("⏱️", "Avg Session", "9 min")
                    MonitoringStatCard("📅", "This Month", "28 sessions")
                }
                Spacer(Modifier.height(16.dp))

                // Session history table
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Recent Sessions", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        sessions.forEach { s ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(s.game, style = MaterialTheme.typography.bodyMedium)
                                    Text(s.date, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                }
                                Text(s.duration, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Text(
                                    "${s.accuracy}%",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        s.accuracy >= 75 -> Color(0xFF2E7D32)
                                        s.accuracy >= 60 -> Color(0xFFEF6C00)
                                        else -> Color(0xFFC62828)
                                    }
                                )
                            }
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MonitoringStatCard(icon: String, label: String, value: String) {
    Card(
        modifier = Modifier
            .height(110.dp)
            .width(105.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, style = MaterialTheme.typography.headlineSmall)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DomainProgressBar(label: String, score: Int) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("$score%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = when {
                score >= 75 -> Color(0xFF2E7D32)
                score >= 60 -> Color(0xFFEF6C00)
                else -> Color(0xFFC62828)
            },
            trackColor = Color(0xFFEEEEEE)
        )
    }
}

@Composable
fun LineTrendChart(data: List<Int>) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(140.dp)) {
        val maxVal = 100f
        val stepX = size.width / (data.size - 1)
        val points = data.mapIndexed { i, v ->
            Offset(i * stepX, size.height - (v / maxVal) * size.height)
        }
        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color(0xFF2E7D32),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }
        points.forEach { p ->
            drawCircle(color = Color(0xFFEF6C00), radius = 8f, center = p)
        }
    }
}
