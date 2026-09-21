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
import com.smritisetu.app.R
import com.smritisetu.app.data.AppStrings
import com.smritisetu.app.data.Localization
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients

data class FamilyMember(
    val nameKey: (AppStrings) -> String,
    val relationKey: (AppStrings) -> String,
    val phone: String,
    val imageRes: Int
)

private fun getFamilyMembers(s: AppStrings) = listOf(
    FamilyMember({ s.nameSunita }, { s.daughterLabel }, "9800000021", R.drawable.mishi_daughter_in_law),
    FamilyMember({ s.nameRahul }, { s.sonLabel }, "9400000008", R.drawable.rahul_son),
    FamilyMember({ s.nameAryan }, { s.grandsonLabel }, "9100000000", R.drawable.bhupen_hazarika_neighbour),
    FamilyMember({ s.nameRina }, { s.doctorLabel }, "9400000001", R.drawable.sunita_baruah_daughter)
)

@Composable
fun FamilyGalleryScreen(navController: NavController) {
    val context = LocalContext.current
    val s = Localization.strings()
    val familyList = getFamilyMembers(s)

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        s.mereApne,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text(s.back) }
                }
                Text(s.tapToCall, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(20.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(familyList) { member ->
                        FamilyCard(member, s) {
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
fun FamilyCard(member: FamilyMember, s: AppStrings, onCall: () -> Unit) {
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
            Image(
                painter = painterResource(id = member.imageRes),
                contentDescription = member.nameKey(s),
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(member.nameKey(s), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(member.relationKey(s), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            }

            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32))
            ) {
                Icon(
                    imageVector = Icons.Default.Call, 
                    contentDescription = "Call", 
                    tint = Color.White, 
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
