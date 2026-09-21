package com.smritisetu.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.data.AlertItem
import com.smritisetu.app.data.AlertSeverity
import com.smritisetu.app.data.AlertsRepository
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton

@Composable
fun AlertsScreen(navController: NavController) {
    val alerts = AlertsRepository.alerts
    val highCount = alerts.count { it.severity == AlertSeverity.HIGH && !it.acknowledged }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.caregiverColors) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🔔 Alerts & Notifications",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }

                if (highCount > 0) {
                    Spacer(Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE).copy(alpha = 0.9f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🚨", style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "$highCount high-priority alert(s) need your attention",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(alerts, key = { it.id }) { alert ->
                                AlertCard(alert = alert, onAcknowledge = {
                                    AlertsRepository.acknowledge(alert.id)
                                })
                            }
                        }
                    }
                }
            }
        }

@Composable
fun AlertCard(alert: AlertItem, onAcknowledge: () -> Unit) {
    val (bgColor, borderColor) = when (alert.severity) {
        AlertSeverity.HIGH -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        AlertSeverity.MEDIUM -> Color(0xFFFFF3E0) to Color(0xFFEF6C00)
        AlertSeverity.LOW -> Color(0xFFF1F8E9) to Color(0xFF2E7D32)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = (if (alert.acknowledged) Color(0xFFF5F5F5) else bgColor).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(alert.icon, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(alert.title, style = MaterialTheme.typography.bodyLarge)
                        Text(alert.time, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(borderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(alert.severity.name, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(alert.description, style = MaterialTheme.typography.bodyMedium)

            if (!alert.acknowledged) {
                Spacer(Modifier.height(10.dp))
                SmritiButton(
                    text = "Mark as Reviewed",
                    onClick = onAcknowledge,
                    modifier = Modifier.align(Alignment.End),
                    containerColor = borderColor
                )
            } else {
                Spacer(Modifier.height(6.dp))
                Text("✅ Reviewed", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E7D32))
            }
        }
    }
}
