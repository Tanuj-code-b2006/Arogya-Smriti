package com.smritisetu.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients

data class InfoRow(val label: String, val value: String)

@Composable
fun PatientProfileScreen(navController: NavController) {
    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.caregiverColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "👤 Patient Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
                Spacer(Modifier.height(16.dp))

                LazyColumnLike {
                    // Profile header card
                    ProfileHeaderCard()
                    Spacer(Modifier.height(16.dp))

                    SectionCard("🩺 Medical Information") {
                        InfoRow("Diagnosis", "Mild Cognitive Impairment (Early Dementia)").Render()
                        InfoRow("Diagnosed On", "12 March 2025").Render()
                        InfoRow("MMSE Score", "22 / 30 (Mild)").Render()
                        InfoRow("Attending Physician", "Dr. Rina Deka, NEIGRIHMS").Render()
                        InfoRow("Known Allergies", "Penicillin").Render()
                    }
                    Spacer(Modifier.height(16.dp))

                    SectionCard("💊 Current Medications") {
                        InfoRow("Donepezil", "5mg — Once daily, morning").Render()
                        InfoRow("Amlodipine", "5mg — Once daily, for BP").Render()
                        InfoRow("Vitamin D3", "60000 IU — Weekly").Render()
                    }
                    Spacer(Modifier.height(16.dp))

                    SectionCard("👨‍👩‍👧 Emergency Contacts") {
                        InfoRow("Primary Caregiver", "Sunita Baruah (Daughter) — 98xxxxxx21").Render()
                        InfoRow("Secondary Contact", "Dr. Rina Deka — 94xxxxxx08").Render()
                        InfoRow("Nearest Health Center", "PHC Sonapur, Kamrup").Render()
                    }
                    Spacer(Modifier.height(16.dp))

                    SectionCard("🗣️ Preferences") {
                        InfoRow("Preferred Language", "Assamese").Render()
                        InfoRow("Voice Interaction", "Enabled").Render()
                        InfoRow("Favourite Games", "Memory Match, AI Chat Companion").Render()
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("👴", style = MaterialTheme.typography.headlineLarge)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Anil Baruah", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Age 74 • Male", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Patient ID: NER-DEM-0042", style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun InfoRow.Render() {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(this@Render.label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(this@Render.value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LazyColumnLike(content: @Composable ColumnScope.() -> Unit) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState), content = content)
}
