package com.smritisetu.app.screens

import androidx.compose.foundation.layout.*
<<<<<<< HEAD
=======
import androidx.compose.foundation.shape.RoundedCornerShape
>>>>>>> Arogya-Smriti/master
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
<<<<<<< HEAD
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton

@Composable
fun CaregiverSectionScreen(navController: NavController) {
    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.caregiverColors) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "👨‍👩‍👧 Caregiver Section",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text("What would you like to do?", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(48.dp))

                SmritiButton(
                    text = "📋 Existing Patients",
                    onClick = { navController.navigate("patient_list") },
                    modifier = Modifier.fillMaxWidth(0.9f).height(72.dp)
                )

                Spacer(Modifier.height(20.dp))

                SmritiButton(
                    text = "➕ Add New Patient",
                    onClick = { navController.navigate("add_patient") },
                    modifier = Modifier.fillMaxWidth(0.9f).height(72.dp),
                    containerColor = MaterialTheme.colorScheme.secondary
                )

                Spacer(Modifier.height(40.dp))
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("← Back")
                }
            }
        }
    }
}
=======

@Composable
fun CaregiverSectionScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("👨‍👩‍👧 Caregiver Section", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        Text("What would you like to do?", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(48.dp))

        Button(
            onClick = { navController.navigate("patient_list") },
            modifier = Modifier.fillMaxWidth(0.9f).height(72.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("📋 Existing Patients", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("add_patient") },
            modifier = Modifier.fillMaxWidth(0.9f).height(72.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("➕ Add New Patient", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(40.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text("← Back")
        }
    }
}
>>>>>>> Arogya-Smriti/master
