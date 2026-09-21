package com.smritisetu.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.data.Localization
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton
import com.smritisetu.app.utils.rememberVoiceNarrator
import kotlinx.coroutines.delay

@Composable
fun AttentionGameScreen(navController: NavController) {
    val s = Localization.strings()
    val narrator = rememberVoiceNarrator()
    LaunchedEffect(Unit) { narrator.say(s.attentionTapIntro) }
    DisposableEffect(Unit) { onDispose { narrator.stop() } }

    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var finished by remember { mutableStateOf(false) }

    val allSymbols = listOf("🍎", "🍌", "🍇", "🍊", "🍍", "🍓", "🍉")
    val targetSymbol = "🍎"
    var board by remember { mutableStateOf(List(9) { allSymbols.random() }) }

    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else {
            finished = true
        }
    }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    s.gameAttentionTap,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text("Tap every $targetSymbol you see!", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                Text("${s.scoreLabel}: $score      Time left: ${timeLeft}s", style = MaterialTheme.typography.bodyMedium)
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
                        SmritiButton(
                            text = s.backToGames,
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.height(64.dp).fillMaxWidth(0.6f)
                        )
                    }
                }
            }
        }
    }
}
