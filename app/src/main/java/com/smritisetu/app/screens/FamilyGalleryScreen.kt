package com.smritisetu.app.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton

data class FamilyMember(
    val name: String,
    val relation: String,
    val phone: String,
    val emoji: String
)

private val familyMembers = listOf(
    FamilyMember("Sunita Baruah", "Daughter (बेटी)", "9800000021", "👩‍💼"),
    FamilyMember("Rahul Baruah", "Son (बेटा)", "9400000008", "👨‍💻"),
    FamilyMember("Aryan", "Grandson (पोता)", "9100000000", "👦"),
    FamilyMember("Dr. Rina Deka", "Doctor (डॉक्टर)", "9400000001", "👩‍⚕️")
)

@Composable
fun FamilyGalleryScreen(navController: NavController) {
    val context = LocalContext.current

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "👨‍👩‍👧 Mere Apne",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
                Text("Tap on a photo to talk to your family!", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(20.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(familyMembers) { member ->
                        FamilyCard(member) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${member.phone}"))
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyCard(member: FamilyMember, onCall: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for photo (using emoji as a fallback)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(member.emoji, style = MaterialTheme.typography.displayMedium)
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(member.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(member.relation, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            }

            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32))
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
    }
}
