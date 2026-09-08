package com.smritisetu.app.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

enum class AlertSeverity { HIGH, MEDIUM, LOW }

data class AlertItem(
    val id: Int,
    val icon: String,
    val title: String,
    val description: String,
    val time: String,
    val severity: AlertSeverity,
    var acknowledged: Boolean = false
)

/**
 * Simple in-memory shared repository so Dashboard, Alerts screen, and the
 * bottom-nav badge all read/write the SAME alert list live.
 * (For production, replace with a ViewModel + Room/Firebase.)
 */
object AlertsRepository {
    val alerts = mutableStateListOf(
        AlertItem(1, "⚠️", "Missed Morning Medicine",
            "Anil did not confirm taking Donepezil at 8:00 AM today.",
            "Today, 8:45 AM", AlertSeverity.HIGH),
        AlertItem(2, "📉", "Attention Score Dropped",
            "Attention Tap accuracy fell to 58% — below 65% baseline for 2 sessions in a row.",
            "Today, 6:10 PM", AlertSeverity.MEDIUM),
        AlertItem(3, "💧", "Low Hydration Reminders Response",
            "3 hydration reminders were dismissed without confirmation this week.",
            "Yesterday, 4:00 PM", AlertSeverity.MEDIUM),
        AlertItem(4, "🕹️", "No Activity Detected",
            "No cognitive game sessions recorded for over 24 hours.",
            "Yesterday, 9:00 PM", AlertSeverity.LOW),
        AlertItem(5, "🏥", "Upcoming Appointment",
            "Reminder: Dr. Rina Deka follow-up appointment is in 2 days.",
            "2 days from now", AlertSeverity.LOW),
        AlertItem(6, "😊", "Positive Trend",
            "Memory Match accuracy improved 12% over the last 7 days — great progress!",
            "This week", AlertSeverity.LOW),
    )

    // The banner shows this whenever a fresh alert comes in live
    var latestIncomingAlert = mutableStateOf<AlertItem?>(null)

    val unreadHighPriorityCount: Int
        get() = alerts.count { it.severity == AlertSeverity.HIGH && !it.acknowledged }

    val totalUnreadCount: Int
        get() = alerts.count { !it.acknowledged }

    fun acknowledge(id: Int) {
        val index = alerts.indexOfFirst { it.id == id }
        if (index != -1) alerts[index] = alerts[index].copy(acknowledged = true)
    }

    fun pushNewAlert(alert: AlertItem) {
        alerts.add(0, alert) // newest on top
        latestIncomingAlert.value = alert
    }
}
