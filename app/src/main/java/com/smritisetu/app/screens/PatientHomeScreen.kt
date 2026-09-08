package com.smritisetu.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients

@Composable
fun PatientHomeScreen(navController: NavController) {
    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "🙏 Good Morning, Anil!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Let's keep your mind active today", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(40.dp))

                HomeCard("🎮", "Play Cognitive Games") { navController.navigate("game_menu") }
                Spacer(Modifier.height(16.dp))
                HomeCard("📝", "Recall My Day") { navController.navigate("daily_recall") }
                Spacer(Modifier.height(16.dp))
                HomeCard("⏰", "My Reminders") { navController.navigate("reminders") }
                Spacer(Modifier.height(16.dp))
                HomeCard("📞", "Call Family") { /* mock */ }
            }
        }
    }
}

@Composable
fun HomeCard(icon: String, label: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(90.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.width(20.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}
