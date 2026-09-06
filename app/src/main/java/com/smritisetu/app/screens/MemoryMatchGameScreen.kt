package com.smritisetu.app.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import kotlinx.coroutines.delay

data class MemoryCard(val id: Int, val symbol: String, var isFlipped: Boolean = false, var isMatched: Boolean = false)

// Culturally familiar symbols (fruits/household items relevant to NER context)
val symbolPool = listOf("🍌", "🍚", "🐘", "🌸", "🍵", "🧣", "🥥", "🎋", "🦚", "🪔")

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
            delay(700)
            val c1 = cards[firstPick!!]
            val c2 = cards[secondPick!!]
            cards = cards.toMutableList().also { list ->
                if (c1.symbol == c2.symbol) {
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
            // --- Simulated AI adaptive difficulty logic ---
            val accuracy = if (moves == 0) 1f else (moves - mistakes).toFloat() / moves
            difficultyMessage = when {
                accuracy > 0.7f && gridSize < 8 -> {
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
        AnimatedGradientBox {
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
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Exit") }
                }
                Text("Moves: $moves    Mistakes: $mistakes", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(columns = GridCells.Fixed(if (gridSize <= 4) 2 else 3)) {
                    items(cards, key = { it.id }) { card ->
                        MemoryCardView(card = card, enabled = firstPick == null || secondPick == null) {
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
    val symbols = symbolPool.shuffled().take(size / 2)
    return (symbols + symbols).shuffled().mapIndexed { index, s -> MemoryCard(id = index, symbol = s) }
}

@Composable
fun MemoryCardView(card: MemoryCard, enabled: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = when {
            card.isMatched -> Color(0xFFA5D6A7)
            card.isFlipped -> Color(0xFFFFF9C4)
            else -> Color(0xFF2E7D32)
        }, label = "cardColor"
    )
    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .background(bgColor, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxSize().padding(4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (card.isFlipped || card.isMatched) card.symbol else "❓",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
}
