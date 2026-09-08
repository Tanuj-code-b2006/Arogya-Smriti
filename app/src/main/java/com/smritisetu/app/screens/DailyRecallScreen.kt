package com.smritisetu.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton

@Composable
fun DailyRecallScreen(navController: NavController) {
    var morningText by remember { mutableStateOf("") }
    var afternoonText by remember { mutableStateOf("") }
    var eveningText by remember { mutableStateOf("") }

    var morningForgot by remember { mutableStateOf(false) }
    var afternoonForgot by remember { mutableStateOf(false) }
    var eveningForgot by remember { mutableStateOf(false) }

    var showResult by remember { mutableStateOf(false) }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📝 Today's Memories",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
                Text(
                    "Try to remember what you did today. It's okay if you forget!",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(24.dp))

                RecallField(
                    label = "☀️ In the Morning...",
                    text = morningText,
                    onValueChange = { morningText = it; morningForgot = false },
                    isForgot = morningForgot,
                    onForgotToggle = { morningForgot = !morningForgot; if (morningForgot) morningText = "" }
                )

                Spacer(Modifier.height(20.dp))

                RecallField(
                    label = "🌤️ In the Afternoon...",
                    text = afternoonText,
                    onValueChange = { afternoonText = it; afternoonForgot = false },
                    isForgot = afternoonForgot,
                    onForgotToggle = { afternoonForgot = !afternoonForgot; if (afternoonForgot) afternoonText = "" }
                )

                Spacer(Modifier.height(20.dp))

                RecallField(
                    label = "🌙 In the Evening...",
                    text = eveningText,
                    onValueChange = { eveningText = it; eveningForgot = false },
                    isForgot = eveningForgot,
                    onForgotToggle = { eveningForgot = !eveningForgot; if (eveningForgot) eveningText = "" }
                )

                Spacer(Modifier.height(32.dp))

                if (!showResult) {
                    SmritiButton(
                        text = "Done for Today ✅",
                        onClick = { showResult = true },
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )
                } else {
                    val answeredCount = listOf(
                        morningText.isNotBlank(),
                        afternoonText.isNotBlank(),
                        eveningText.isNotBlank()
                    ).count { it }
                    
                    val points = answeredCount * 10
                    
                    ResultCard(answeredCount, points) {
                        navController.popBackStack()
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RecallField(
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
    isForgot: Boolean,
    onForgotToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = onForgotToggle) {
                    Text(if (isForgot) "I remember! 😊" else "🤷‍♂️ I forgot", color = MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(Modifier.height(8.dp))
            if (!isForgot) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onValueChange,
                    placeholder = { Text("What did you do?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                Text(
                    "No worries! We can try again tomorrow.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun ResultCard(count: Int, points: Int, onDismiss: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (count) {
                    3 -> "🎉 Fantastic!"
                    2 -> "🌟 Great Job!"
                    1 -> "👍 Well Done!"
                    else -> "🌈 Keep Trying!"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF2E7D32)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = when (count) {
                    3 -> "You remembered everything today!"
                    2 -> "You remembered most of your day!"
                    1 -> "You remembered part of your day!"
                    else -> "It's okay, let's try more tomorrow!"
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "You earned $points points!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF6C00)
            )
            Spacer(Modifier.height(24.dp))
            SmritiButton(
                text = "See you later!",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
