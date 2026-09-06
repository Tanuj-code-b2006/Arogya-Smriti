package com.smritisetu.app.screens

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import kotlinx.coroutines.delay

@Composable
fun AttentionGameScreen(navController: NavController) {
    val targetSymbol = "🍎"
    val allSymbols = listOf("🍎", "🍌", "🍇", "🍊", "🥭", "🍍")
    var board by remember { mutableStateOf(List(9) { allSymbols.random() }) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        finished = true
    }

    AnimatedScreen {
        AnimatedGradientBox {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "👀 Attention Tap",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Tap every $targetSymbol you see!", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                Text("Score: $score      Time left: ${timeLeft}s", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(20.dp))

                if (!finished) {
                    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                        items(board.size) { idx ->
                            Card(
                                onClick = {
                                    if (board[idx] == targetSymbol) {
                                        score++
                                    } else {
                                        score = maxOf(0, score - 1)
                                    }
                                    board = board.toMutableList().also { it[idx] = allSymbols.random() }
                                },
                                modifier = Modifier
                                    .padding(10.dp)
                                    .aspectRatio(1f)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(board[idx], style = MaterialTheme.typography.headlineLarge)
                                }
                            }
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉 Time's up!", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Final Score: $score",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(40.dp))
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.height(64.dp).fillMaxWidth(0.6f)
                        ) {
                            Text("Back to Games")
                        }
                    }
                }
            }
        }
    }
}
