package com.smritisetu.app.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smritisetu.app.R
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import kotlinx.coroutines.delay

data class FamilyMemberInfo(
    val imageRes: Int,
    val name: String,
    val relation: String
)

data class MemoryCard(
    val id: Int,
    val info: FamilyMemberInfo,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

// Updated Family photos with names and relations
val familyPhotoPool = listOf(
    FamilyMemberInfo(R.drawable.rahul_son, "Rahul", "Son"),
    FamilyMemberInfo(R.drawable.sunita_baruah_daughter, "Ishani", "Wife"),
    FamilyMemberInfo(R.drawable.ishan_nephew, "Ishan", "Nephew"),
    FamilyMemberInfo(R.drawable.mishi_daughter_in_law, "Mishi", "Daughter-in-law"),
    FamilyMemberInfo(R.drawable.bhupen_hazarika_neighbour, "Bhupen Hazarika", "Neighbour")
)

@Composable
fun MemoryMatchGameScreen(navController: NavController) {
    var gridSize by remember { mutableIntStateOf(4) } // starts easy: 4 cards (2 pairs)
    var round by remember { mutableIntStateOf(1) }
    var cards by remember { mutableStateOf(generateCards(gridSize)) }
    var firstPick by remember { mutableStateOf<Int?>(null) }
    var secondPick by remember { mutableStateOf<Int?>(null) }
    var moves by remember { mutableIntStateOf(0) }
    var mistakes by remember { mutableIntStateOf(0) }
    var showRoundComplete by remember { mutableStateOf(false) }
    var difficultyMessage by remember { mutableStateOf("") }

    val allMatched = cards.all { it.isMatched }

    LaunchedEffect(firstPick, secondPick) {
        if (firstPick != null && secondPick != null) {
            delay(800)
            val c1 = cards[firstPick!!]
            val c2 = cards[secondPick!!]
            cards = cards.toMutableList().also { list ->
                if (c1.info.imageRes == c2.info.imageRes) {
                    list[firstPick!!] = c1.copy(isMatched = true)
                    list[secondPick!!] = c2.copy(isMatched = true)
                } else {
                    mistakes++
                    list[firstPick!!] = c1.copy(isFlipped = false)
                    list[secondPick!!] = c2.copy(isFlipped = false)
                }
            }
            firstPick = null
            secondPick = null
        }
    }

    LaunchedEffect(allMatched) {
        if (allMatched && cards.isNotEmpty()) {
            val accuracy = if (moves == 0) 1f else (moves - mistakes).toFloat() / moves
            difficultyMessage = when {
                accuracy > 0.7f && gridSize < 10 -> {
                    gridSize += 2
                    "Great job! 🎉 Increasing difficulty next round."
                }
                accuracy < 0.4f && gridSize > 4 -> {
                    gridSize -= 2
                    "Let's make the next round a bit easier."
                }
                else -> "Nicely done! Keeping the same level."
            }
            showRoundComplete = true
        }
    }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🧩 Memory Match",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Exit") }
                }
                Text("Moves: $moves    Mistakes: $mistakes", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (gridSize <= 4) 2 else 3),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cards, key = { it.id }) { card ->
                        MemoryCardView(card = card) {
                            if (!card.isFlipped && !card.isMatched && secondPick == null) {
                                cards = cards.toMutableList().also { it[card.id] = card.copy(isFlipped = true) }
                                moves++
                                if (firstPick == null) firstPick = card.id else secondPick = card.id
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRoundComplete) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Round Complete! 🎉") },
            text = { Text("$difficultyMessage\n\nAI Insight: Accuracy ${((moves - mistakes).toFloat() / moves * 100).toInt()}%") },
            confirmButton = {
                Button(onClick = {
                    round++
                    cards = generateCards(gridSize)
                    moves = 0; mistakes = 0
                    showRoundComplete = false
                }) { Text("Next Round") }
            },
            dismissButton = {
                TextButton(onClick = { navController.popBackStack() }) { Text("Finish") }
            }
        )
    }
}

fun generateCards(size: Int): List<MemoryCard> {
    val pool = familyPhotoPool.shuffled().take(size / 2)
    return (pool + pool).shuffled().mapIndexed { index, info -> 
        MemoryCard(id = index, info = info) 
    }
}

@Composable
fun MemoryCardView(card: MemoryCard, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = when {
            card.isMatched -> Color(0xFFA5D6A7)
            card.isFlipped -> Color.White
            else -> MaterialTheme.colorScheme.primary
        }, label = "cardColor"
    )
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (card.isFlipped || card.isMatched) 180.dp else 140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (card.isFlipped || card.isMatched) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = card.info.imageRes),
                        contentDescription = card.info.name,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = card.info.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "(${card.info.relation})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(text = "❓", fontSize = 48.sp, color = Color.White)
            }
        }
    }
}
