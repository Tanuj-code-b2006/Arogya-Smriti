package com.smritisetu.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smritisetu.app.data.AlertItem
import com.smritisetu.app.data.AlertSeverity
import kotlinx.coroutines.delay

/**
 * Slides down from the top like a real push notification whenever
 * AlertsRepository.latestIncomingAlert changes. Auto-dismisses after 4s,
 * or tap to jump straight to the Alerts screen.
 */
@Composable
fun NotificationBanner(
    alert: AlertItem?,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    LaunchedEffect(alert) {
        if (alert != null) {
            delay(4500)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = alert != null,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        if (alert != null) {
            val accentColor = when (alert.severity) {
                AlertSeverity.HIGH -> Color(0xFFC62828)
                AlertSeverity.MEDIUM -> Color(0xFFEF6C00)
                AlertSeverity.LOW -> Color(0xFF2E7D32)
            }
            Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Card(
                    onClick = { onClick(); onDismiss() },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(alert.icon, style = MaterialTheme.typography.headlineMedium)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "New Alert: ${alert.title}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = accentColor
                            )
                            Text(
                                alert.description,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Text("✕", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}