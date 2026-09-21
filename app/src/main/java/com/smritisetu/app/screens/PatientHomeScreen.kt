package com.smritisetu.app.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.data.Localization
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients

@Composable
fun PatientHomeScreen(navController: NavController) {
    val s = Localization.strings()
    
    // Using a static state for the demo to persist within the session
    // In a real app, this might come from a ViewModel or Database
    var showCaregiverNotification by remember { mutableStateOf(true) }

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
                    text = "${s.goodMorning}, ${s.patientName}!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = s.keepMindActive, 
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(30.dp))

                if (showCaregiverNotification) {
                    NotificationFromCaregiverCard(
                        title = s.caregiverNotification,
                        message = s.missedMedMsg,
                        onRead = { showCaregiverNotification = false }
                    )
                    Spacer(Modifier.height(24.dp))
                }

                HomeCard("📋", s.dailyPlan) { navController.navigate("create_routine") }
                Spacer(Modifier.height(16.dp))
                HomeCard("🎮", s.playGames) { navController.navigate("game_menu") }
                Spacer(Modifier.height(16.dp))
                HomeCard("📝", s.recallDay) { navController.navigate("daily_recall") }
                Spacer(Modifier.height(16.dp))
                HomeCard("👨‍👩‍👧", s.mereApne) { navController.navigate("family_gallery") }
            }
        }
    }
}

@Composable
fun NotificationFromCaregiverCard(title: String, message: String, onRead: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val s = Localization.strings()
    
    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth().height(90.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Text("📩", style = MaterialTheme.typography.displayMedium)
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .offset(x = 4.dp, y = (-4).dp)
                        .alpha(alpha)
                        .background(Color.Red, CircleShape)
                )
            }
            Spacer(Modifier.width(20.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { 
                // Do nothing on dismiss to force clicking OK
            },
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = { Text(message, style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                Button(onClick = { 
                    showDialog = false
                    onRead() // This will trigger the removal of the card from the home screen
                }) {
                    Text(s.ok)
                }
            }
        )
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
