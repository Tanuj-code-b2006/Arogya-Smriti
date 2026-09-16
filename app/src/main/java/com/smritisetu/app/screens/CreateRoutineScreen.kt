package com.smritisetu.app.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton

@Composable
fun CreateRoutineScreen(navController: NavController) {
    val context = LocalContext.current
    var routineGoal by remember { mutableStateOf("") }
    var medicineDetails by remember { mutableStateOf("") }
    var doctorAppointment by remember { mutableStateOf("") }

    var routineForgot by remember { mutableStateOf(false) }
    var medicineForgot by remember { mutableStateOf(false) }
    var doctorForgot by remember { mutableStateOf(false) }

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
                        "📋 My Daily Plan",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
                Text(
                    "Let's set up your schedule for a healthy day!",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(24.dp))

                // Section 1: Routine Goal
                RoutineInputCard(
                    title = "🌈 Today's Plan",
                    value = routineGoal,
                    onValueChange = { routineGoal = it; routineForgot = false },
                    placeholder = "e.g. Walking in the park...",
                    isForgot = routineForgot,
                    onForgotToggle = { routineForgot = !routineForgot; if (routineForgot) routineGoal = "" }
                )

                Spacer(Modifier.height(20.dp))

                // Section 2: Medicines
                RoutineInputCard(
                    title = "💊 My Medicines",
                    value = medicineDetails,
                    onValueChange = { medicineDetails = it; medicineForgot = false },
                    placeholder = "Name and time",
                    isForgot = medicineForgot,
                    onForgotToggle = { medicineForgot = !medicineForgot; if (medicineForgot) medicineDetails = "" }
                )

                Spacer(Modifier.height(20.dp))

                // Section 3: Doctor Appointment
                RoutineInputCard(
                    title = "🏥 Doctor Visit",
                    value = doctorAppointment,
                    onValueChange = { doctorAppointment = it; doctorForgot = false },
                    placeholder = "Doctor's name and time",
                    isForgot = doctorForgot,
                    onForgotToggle = { doctorForgot = !doctorForgot; if (doctorForgot) doctorAppointment = "" }
                )

                Spacer(Modifier.height(32.dp))

                SmritiButton(
                    text = "Save My Routine ✨",
                    onClick = {
                        if (routineGoal.isNotBlank() || medicineDetails.isNotBlank() || doctorAppointment.isNotBlank() ||
                            routineForgot || medicineForgot || doctorForgot) {
                            Toast.makeText(context, "Routine updated! We'll remind you.", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Please enter some details or mark as forgot", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
                
                Spacer(Modifier.height(24.dp))
                Text(
                    "We are here to help you remember! 😊",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun RoutineInputCard(
    title: String, 
    value: String, 
    onValueChange: (String) -> Unit, 
    placeholder: String,
    isForgot: Boolean,
    onForgotToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = onForgotToggle) {
                    Text(if (isForgot) "I remember! 😊" else "🤷‍♂️ I forgot", color = MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(Modifier.height(12.dp))
            if (!isForgot) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = { Text(placeholder) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            } else {
                Text("No problem, we'll check again later!", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
