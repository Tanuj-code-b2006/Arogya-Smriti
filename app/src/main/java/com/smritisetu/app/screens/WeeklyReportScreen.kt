package com.smritisetu.app.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun WeeklyReportScreen(navController: NavController) {
    val context = LocalContext.current
    
    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.caregiverColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📄 Weekly Report",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Patient: Anil Baruah", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Report Period: Sep 1 - Sep 7, 2026", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(Modifier.height(12.dp))
                        
                        Box(
                            modifier = Modifier.background(
                                Color(0xFFE8F5E9), 
                                RoundedCornerShape(8.dp)
                            ).padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Current Status: STABLE", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Report Summary Section
                ReportSection("🧠 Cognitive Summary", "Average accuracy across all games is 78%. Noted improvement in 'Memory Match' by 12% this week. 'Attention Tap' scores were slightly lower on Wednesday but recovered by Friday.")
                
                Spacer(Modifier.height(16.dp))
                
                ReportSection("💊 Medication Adherence", "Anil ji has been very consistent. 95% adherence rate. Missed one morning dose of Donepezil on Monday.")
                
                Spacer(Modifier.height(16.dp))
                
                ReportSection("📋 Daily Activities", "Completed 28 cognitive sessions this week. Morning walks were taken daily except during the rain on Thursday.")

                Spacer(Modifier.height(32.dp))

                Text("Send Report To:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    SmritiButton(
                        text = "Physician",
                        onClick = { Toast.makeText(context, "Report sent to Dr. Rina Deka", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.weight(1f),
                        icon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )
                    SmritiButton(
                        text = "Relatives",
                        onClick = { Toast.makeText(context, "Report shared with family members", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.secondary,
                        icon = { Icon(Icons.Default.Share, contentDescription = null) }
                    )
                }
                
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ReportSection(title: String, description: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
