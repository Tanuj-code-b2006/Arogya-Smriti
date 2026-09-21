package com.smritisetu.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.data.Localization
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton
import com.smritisetu.app.utils.rememberVoiceNarrator

data class RoutineItem(val id: Int, val task: String, val icon: String)

@Composable
fun RoutineSequencerScreen(navController: NavController) {
    val s = Localization.strings()
    val narrator = rememberVoiceNarrator()
    LaunchedEffect(Unit) { narrator.say(s.routineOrderIntro) }
    DisposableEffect(Unit) { onDispose { narrator.stop() } }

    var items by remember {
        mutableStateOf(
            listOf(
                RoutineItem(1, "Wake Up", "🌅"),
                RoutineItem(2, "Brush Teeth", "🪥"),
                RoutineItem(3, "Have Breakfast", "🥣"),
                RoutineItem(4, "Take Medicine", "💊")
            ).shuffled()
        )
    }

    var showResult by remember { mutableStateOf(false) }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        s.gameRoutineOrder,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text(s.exit) }
                }
                Text("Order your morning routine!", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.icon, style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.width(16.dp))
                                Text(item.task, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                SmritiButton(
                    text = "Check Order ✅",
                    onClick = { showResult = true },
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
            }
        }
    }

    if (showResult) {
        val isCorrect = items.map { it.id } == listOf(1, 2, 3, 4)
        AlertDialog(
            onDismissRequest = { showResult = false },
            title = { Text(if (isCorrect) "Perfect! 🌟" else "Almost there! 💪") },
            text = { Text(if (isCorrect) "You remembered the order perfectly!" else "The order was a bit mixed up. Keep practicing!") },
            confirmButton = {
                Button(onClick = {
                    if (isCorrect) navController.popBackStack() else {
                        items = items.shuffled()
                        showResult = false
                    }
                }) {
                    Text(if (isCorrect) s.backToGames else "Try Again")
                }
            }
        )
    }
}
