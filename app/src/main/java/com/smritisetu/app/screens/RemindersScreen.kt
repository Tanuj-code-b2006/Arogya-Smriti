package com.smritisetu.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen

data class Reminder(val icon: String, val title: String, val time: String, var done: Boolean = false)

@Composable
fun RemindersScreen(navController: NavController) {
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

    AnimatedScreen {
        AnimatedGradientBox {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    "⏰ Today's Reminders",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                reminders.forEachIndexed { index, reminder ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(reminder.icon, style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(reminder.title, style = MaterialTheme.typography.bodyLarge)
                                    Text(reminder.time, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Checkbox(checked = reminder.done, onCheckedChange = { checked ->
                                reminders = reminders.toMutableList().also {
                                    it[index] = reminder.copy(done = checked)
                                }
                            })
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Button(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Back")
                }
            }
        }
    }
}
