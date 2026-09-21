package com.smritisetu.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.utils.rememberVoiceNarrator

data class Reminder(val icon: String, val title: String, val time: String, var done: Boolean = false)

@Composable
fun RemindersScreen(navController: NavController) {
    val narrator = rememberVoiceNarrator()
    var reminders by remember {
        mutableStateOf(
            listOf(
                Reminder("💊", "Take Blood Pressure Medicine", "8:00 AM"),
                Reminder("💧", "Drink a glass of water", "10:00 AM"),
                Reminder("🍽️", "Lunch time", "1:00 PM"),
                Reminder("🏥", "Doctor appointment (Dr. Rina)", "4:30 PM"),
            )
        )
    }

    // Greet the patient in their language when the screen opens
    LaunchedEffect(Unit) {
        narrator.say("Here are your reminders for today.")
    }
    DisposableEffect(Unit) { onDispose { narrator.stop() } }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "⏰ My Reminders",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
                
                Spacer(Modifier.height(16.dp))
                
                reminders.forEachIndexed { index, reminder ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(reminder.icon, style = MaterialTheme.typography.displaySmall)
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(reminder.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                    Text(reminder.time, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                }
                            }
                            
                            IconButton(onClick = {
                                narrator.say("${reminder.title}, at ${reminder.time}")
                            }) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = "Read aloud", tint = MaterialTheme.colorScheme.primary)
                            }
                            
                            Checkbox(
                                checked = reminder.done,
                                onCheckedChange = { checked ->
                                    reminders = reminders.toMutableList().also {
                                        it[index] = reminder.copy(done = checked)
                                    }
                                    if (checked) narrator.say("Marked done. Well done!")
                                },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }
    }
}
