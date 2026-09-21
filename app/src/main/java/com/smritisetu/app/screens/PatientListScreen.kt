package com.smritisetu.app.screens

<<<<<<< HEAD
=======
import androidx.compose.foundation.background
>>>>>>> Arogya-Smriti/master
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
<<<<<<< HEAD
import androidx.compose.ui.text.font.FontWeight
=======
>>>>>>> Arogya-Smriti/master
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smritisetu.app.data.PatientEntity
import com.smritisetu.app.data.PatientViewModel
<<<<<<< HEAD
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
=======
>>>>>>> Arogya-Smriti/master

@Composable
fun PatientListScreen(
    navController: NavController,
    viewModel: PatientViewModel = viewModel()
) {
    val patients by viewModel.patients.collectAsState()

<<<<<<< HEAD
    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.caregiverColors) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📋 Existing Patients",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
                Spacer(Modifier.height(16.dp))

                if (patients.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No patients yet. Add one!", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    // Table header
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp)) {
                            TableCell("Name", weight = 1.4f, header = true)
                            TableCell("Age", weight = 0.6f, header = true)
                            TableCell("Gen", weight = 0.7f, header = true)
                            TableCell("Phone", weight = 1.3f, header = true)
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(patients, key = { it.id }) { patient ->
                            PatientRow(patient) {
                                navController.navigate("caregiver_dashboard")
                            }
                        }
=======
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📋 Existing Patients", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
        }
        Spacer(Modifier.height(16.dp))

        if (patients.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No patients yet. Add one!", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            // Table header
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp)) {
                    TableCell("Name", weight = 1.4f, header = true)
                    TableCell("Age", weight = 0.6f, header = true)
                    TableCell("Gender", weight = 0.8f, header = true)
                    TableCell("Phone", weight = 1.2f, header = true)
                }
            }
            Spacer(Modifier.height(6.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(patients, key = { it.id }) { patient ->
                    PatientRow(patient) {
                        // Anil Baruah (ya koi bhi patient) par click karte hi
                        // pehle se bana hua Caregiver Dashboard khulega
                        navController.navigate("caregiver_dashboard")
>>>>>>> Arogya-Smriti/master
                    }
                }
            }
        }
    }
}

@Composable
fun PatientRow(patient: PatientEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
<<<<<<< HEAD
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TableCell(patient.name, weight = 1.4f)
                TableCell("${patient.age}", weight = 0.6f)
                TableCell(patient.gender.take(1), weight = 0.7f) // Just M/F
                TableCell(patient.phone, weight = 1.3f)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "📍 ${patient.address}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
=======
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                TableCell(patient.name, weight = 1.4f)
                TableCell("${patient.age}", weight = 0.6f)
                TableCell(patient.gender, weight = 0.8f)
                TableCell(patient.phone, weight = 1.2f)
            }
            Text(
                "📍 ${patient.address}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
>>>>>>> Arogya-Smriti/master
            )
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float, header: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
<<<<<<< HEAD
        style = if (header) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyMedium,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
        color = if (header) Color.White else Color.Black
    )
}
=======
        style = if (header) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
        color = if (header) Color.White else Color.Black
    )
}
>>>>>>> Arogya-Smriti/master
