package com.smritisetu.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen

data class GameItem(val icon: String, val title: String, val route: String?, val level: String)

@Composable
fun GameMenuScreen(navController: NavController) {
    val games = listOf(
        GameItem("🧩", "Memory Match", "memory_game", "Level 2 (Adaptive)"),
        GameItem("👀", "Attention Tap", "attention_game", "Level 1"),
        GameItem("🗓️", "Daily Routine Recall", null, "Coming up next"),
        GameItem("🥭", "Pattern & Object ID", null, "Coming up next"),
    )

    AnimatedScreen {
        AnimatedGradientBox {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    "Choose a Game",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(20.dp))
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(games) { game ->
                        Card(
                            onClick = { game.route?.let { navController.navigate(it) } },
                            modifier = Modifier
                                .padding(10.dp)
                                .height(160.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(5.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(game.icon, style = MaterialTheme.typography.headlineLarge)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    game.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    game.level, style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
