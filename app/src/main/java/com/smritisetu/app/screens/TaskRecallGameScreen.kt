package com.smritisetu.app.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton
import com.smritisetu.app.utils.rememberVoiceNarrator
import com.smritisetu.app.data.Localization
import com.smritisetu.app.data.AppStrings

data class GameTask(
    val id: Int, 
    val titleKey: (AppStrings) -> String, 
    val icon: String,
    val optionsKey: (AppStrings) -> List<String>
)

@Composable
fun TaskRecallGameScreen(navController: NavController) {
    val s = Localization.strings()
    val narrator = rememberVoiceNarrator()
    DisposableEffect(Unit) { onDispose { narrator.stop() } }

    val tasks = remember(s) {
        listOf(
            GameTask(1, { it.task1 }, "🥛", { it.t1opts }),
            GameTask(2, { it.task2 }, "📞", { it.t2opts }),
            GameTask(3, { it.task3 }, "🪴", { it.t3opts }),
            GameTask(4, { it.task4 }, "🐈", { it.t4opts }),
            GameTask(5, { it.task5 }, "💊", { it.t5opts })
        ).shuffled()
    }

    var isRecallPhase by remember { mutableStateOf(false) }
    var currentTaskIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    LaunchedEffect(isRecallPhase, currentTaskIndex) {
        if (!isRecallPhase) {
            narrator.say(s.taskRecallIntro)
        } else if (!isFinished) {
            narrator.say(s.whatWasTask)
        }
    }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        s.gameDailyTasks,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text(s.exit, fontSize = 18.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (!isRecallPhase) {
                    // Morning Phase: Memorize
                    Text(
                        s.morningPhase,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        s.memorizeTasks,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    tasks.forEach { task ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(task.icon, fontSize = 32.sp)
                                Spacer(Modifier.width(16.dp))
                                Text(task.titleKey(s), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    SmritiButton(
                        text = s.memorizedDone,
                        onClick = { isRecallPhase = true },
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )

                } else if (!isFinished) {
                    // Evening Phase: Recall
                    val currentTask = tasks[currentTaskIndex]
                    val options = currentTask.optionsKey(s)
                    val shuffledOptions = remember(currentTaskIndex, s) { options.shuffled() }

                    Text(
                        s.eveningPhase,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${s.questionLabel} ${currentTaskIndex + 1} ${s.ofLabel} ${tasks.size}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(Modifier.height(24.dp))

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier.size(120.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(currentTask.icon, fontSize = 64.sp)
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(
                        s.whatWasTask,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    shuffledOptions.forEach { option ->
                        val isSelected = selectedOption == option
                        val isCorrect = option == currentTask.titleKey(s)
                        
                        val buttonColor = when {
                            !showFeedback -> Color.White.copy(alpha = 0.9f)
                            isCorrect -> Color(0xFFC8E6C9)
                            isSelected && !isCorrect -> Color(0xFFFFCDD2)
                            else -> Color.White.copy(alpha = 0.9f)
                        }

                        Card(
                            onClick = {
                                if (!showFeedback) {
                                    selectedOption = option
                                    showFeedback = true
                                    if (isCorrect) score++
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = buttonColor),
                            elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp)
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (showFeedback) {
                        Spacer(Modifier.height(24.dp))
                        SmritiButton(
                            text = if (currentTaskIndex < tasks.size - 1) s.next else s.seeResults,
                            onClick = {
                                if (currentTaskIndex < tasks.size - 1) {
                                    currentTaskIndex++
                                    selectedOption = null
                                    showFeedback = false
                                } else {
                                    isFinished = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(64.dp)
                        )
                    }

                } else {
                    // Results
                    FinalTaskResultCard(score = score, total = tasks.size, s = s) {
                        navController.popBackStack()
                    }
                }
                
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun FinalTaskResultCard(score: Int, total: Int, s: AppStrings, onBack: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (score == total) "🏆 Perfect Recall!" else s.greatJob,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Text(s.youScored, style = MaterialTheme.typography.bodyLarge)
            Text("$score / $total", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)
            Text(s.pointsLabel, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(40.dp))
            SmritiButton(
                text = s.backToGames,
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )
        }
    }
}
