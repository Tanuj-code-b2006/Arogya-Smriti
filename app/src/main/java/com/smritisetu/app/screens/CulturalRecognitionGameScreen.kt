package com.smritisetu.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.background
data class CulturalItem(
    val emoji: String,
    val category: String,   // Food / Dance / Clothing / Festival
    val correctName: String,
    val options: List<String>
)

private val culturalItems = listOf(
    CulturalItem("🍜", "Food", "Momos",
        listOf("Momos", "Biryani", "Dosa", "Samosa")),
    CulturalItem("💃", "Dance", "Bihu Dance",
        listOf("Bihu Dance", "Bharatanatyam", "Garba", "Kathak")),
    CulturalItem("🧣", "Clothing", "Muga Silk Mekhela Sador",
        listOf("Muga Silk Mekhela Sador", "Banarasi Saree", "Lehenga", "Salwar Kameez")),
    CulturalItem("🥭", "Food", "Khar (Assamese dish)",
        listOf("Khar", "Sambar", "Rasam", "Dal Makhani")),
    CulturalItem("🎭", "Dance", "Manipuri Raas Leela",
        listOf("Manipuri Raas Leela", "Kathakali", "Odissi", "Mohiniyattam")),
    CulturalItem("👘", "Clothing", "Naga Shawl",
        listOf("Naga Shawl", "Pashmina Shawl", "Phulkari Dupatta", "Bandhani Saree")),
    CulturalItem("🪔", "Festival", "Rongali Bihu",
        listOf("Rongali Bihu", "Diwali", "Pongal", "Onam")),
    CulturalItem("🎋", "Clothing", "Khasi Jainsem",
        listOf("Khasi Jainsem", "Ghagra Choli", "Anarkali Suit", "Dhoti Kurta")),
)

@Composable
fun CulturalRecognitionGameScreen(navController: NavController) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    val shuffledItems = remember { culturalItems.shuffled() }
    val item = shuffledItems[currentIndex]
    val shuffledOptions = remember(currentIndex) { item.options.shuffled() }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🎎 Know Your Culture", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = { navController.popBackStack() }) { Text("Exit") }
        }

        if (!finished) {
            Text("Item ${currentIndex + 1} of ${shuffledItems.size}   •   Score: $score",
                style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Category badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEF6C00), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(item.category, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    modifier = Modifier.size(160.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(item.emoji, style = MaterialTheme.typography.headlineLarge)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("What is this called?", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))

                shuffledOptions.forEach { option ->
                    val isSelected = option == selectedAnswer
                    val isCorrectOption = option == item.correctName
                    val bg = when {
                        !showFeedback -> Color.White
                        isSelected && isCorrectOption -> Color(0xFFC8E6C9)
                        isSelected && !isCorrectOption -> Color(0xFFFFCDD2)
                        isCorrectOption -> Color(0xFFC8E6C9)
                        else -> Color.White
                    }
                    Card(
                        onClick = {
                            if (!showFeedback) {
                                selectedAnswer = option
                                showFeedback = true
                                if (option == item.correctName) score++
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = bg),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Text(option, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                }

                if (showFeedback) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (selectedAnswer == item.correctName) "✅ Correct!" else "It's called ${item.correctName}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedAnswer == item.correctName) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            selectedAnswer = null
                            showFeedback = false
                            if (currentIndex < shuffledItems.size - 1) {
                                currentIndex++
                            } else {
                                finished = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(if (currentIndex < shuffledItems.size - 1) "Next →" else "See Results",
                            style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        } else {
            Spacer(Modifier.height(40.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("🎉", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(12.dp))
                Text("You scored $score out of ${shuffledItems.size}!",
                    style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text("Keep exploring your rich North-Eastern heritage every day!",
                    style = MaterialTheme.typography.bodyLarge, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = {
                        currentIndex = 0; score = 0; finished = false; selectedAnswer = null; showFeedback = false
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Play Again", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(14.dp)) {
                    Text("Back to Games")
                }
            }
        }
    }
}