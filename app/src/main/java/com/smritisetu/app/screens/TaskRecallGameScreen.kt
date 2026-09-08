package com.smritisetu.app.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton
import kotlinx.coroutines.delay
import kotlin.random.Random

data class DailyTask(
    val icon: String,
    val text: String,
    val options: List<String>
)

private val todaysTasks = listOf(
    DailyTask("🪴", "Water the tulsi plant in the courtyard",
        listOf("Water the tulsi plant", "Feed the chickens", "Sweep the yard", "Fold the laundry")),
    DailyTask("📞", "Call your daughter Sunita in the evening",
        listOf("Call your daughter", "Call the doctor", "Call your neighbor", "Call your son")),
    DailyTask("💊", "Take the evening medicine after dinner",
        listOf("Take medicine after lunch", "Take medicine after dinner", "Take medicine before breakfast", "Skip medicine today")),
    DailyTask("📚", "Read two pages of your favorite book",
        listOf("Read two pages of a book", "Write a letter", "Watch television", "Listen to the radio")),
    DailyTask("🚶", "Take a short walk in the garden after lunch",
        listOf("Take a walk after lunch", "Take a walk before breakfast", "Do yoga in the morning", "Rest all day")),
)

enum class GamePhase { MORNING_BRIEFING, WAITING, EVENING_RECALL, RESULTS }

@Composable
fun TaskRecallGameScreen(navController: NavController) {
    var phase by remember { mutableStateOf(GamePhase.MORNING_BRIEFING) }
    var currentTaskIndex by remember { mutableIntStateOf(0) }
    var recallIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🧠 Yaad Rakho",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Exit") }
                }
                Spacer(Modifier.height(12.dp))

                AnimatedContent(
                    targetState = phase,
                    transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                    label = "phaseTransition",
                    modifier = Modifier.weight(1f)
                ) { currentPhase ->
                    when (currentPhase) {
                        GamePhase.MORNING_BRIEFING -> MorningBriefingStep(
                            task = todaysTasks[currentTaskIndex],
                            taskNumber = currentTaskIndex + 1,
                            totalTasks = todaysTasks.size,
                            onNext = {
                                if (currentTaskIndex < todaysTasks.size - 1) {
                                    currentTaskIndex++
                                } else {
                                    phase = GamePhase.WAITING
                                }
                            }
                        )
                        GamePhase.WAITING -> WaitingForEveningStep(
                            onContinue = { phase = GamePhase.EVENING_RECALL }
                        )
                        GamePhase.EVENING_RECALL -> EveningRecallStep(
                            task = todaysTasks[recallIndex],
                            questionNumber = recallIndex + 1,
                            totalQuestions = todaysTasks.size,
                            selectedAnswer = selectedAnswer,
                            showFeedback = showFeedback,
                            onSelect = { answer ->
                                if (!showFeedback) {
                                    selectedAnswer = answer
                                    showFeedback = true
                                    if (answer == todaysTasks[recallIndex].options[0]) score++
                                }
                            },
                            onNext = {
                                selectedAnswer = null
                                showFeedback = false
                                if (recallIndex < todaysTasks.size - 1) {
                                    recallIndex++
                                } else {
                                    phase = GamePhase.RESULTS
                                }
                            }
                        )
                        GamePhase.RESULTS -> ResultsStep(
                            score = score,
                            total = todaysTasks.size,
                            onFinish = { navController.popBackStack() },
                            onRetry = {
                                currentTaskIndex = 0; recallIndex = 0; score = 0
                                phase = GamePhase.MORNING_BRIEFING
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MorningBriefingStep(task: DailyTask, taskNumber: Int, totalTasks: Int, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("☀️ Good Morning! Remember today's tasks", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Text("Task $taskNumber of $totalTasks", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
        Spacer(Modifier.height(32.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(task.icon, style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(16.dp))
                Text(task.text, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("Try to remember this — you'll be asked about it later!",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1f))

        SmritiButton(
            text = if (taskNumber < totalTasks) "Got It, Next Task →" else "I'll Remember Everything",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(72.dp)
        )
    }
}

@Composable
private fun WaitingForEveningStep(onContinue: () -> Unit) {
    var secondsLeft by remember { mutableIntStateOf(3) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 60.dp)) {
        Text("🌇", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text("Evening has come...", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Let's see how many tasks you remember!", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(40.dp))
        if (secondsLeft > 0) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            SmritiButton(
                text = "Start Recall Challenge",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(72.dp)
            )
        }
    }
}

@Composable
private fun EveningRecallStep(
    task: DailyTask,
    questionNumber: Int,
    totalQuestions: Int,
    selectedAnswer: String?,
    showFeedback: Boolean,
    onSelect: (String) -> Unit,
    onNext: () -> Unit
) {
    val correctAnswer = task.options[0]
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("Recall Task $questionNumber of $totalQuestions", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.size(100.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(task.icon, style = MaterialTheme.typography.displaySmall)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("What was this task about?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        task.options.shuffled(Random(task.hashCode())).forEach { option ->
            val isSelected = option == selectedAnswer
            val isCorrectOption = option == correctAnswer
            val bg = when {
                !showFeedback -> MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                isSelected && isCorrectOption -> Color(0xFFC8E6C9)
                isSelected && !isCorrectOption -> Color(0xFFFFCDD2)
                isCorrectOption -> Color(0xFFC8E6C9)
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            }
            Card(
                onClick = { onSelect(option) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = bg),
                elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
            ) {
                Text(option, modifier = Modifier.padding(18.dp), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
        }

        if (showFeedback) {
            Spacer(Modifier.height(16.dp))
            Text(
                if (selectedAnswer == correctAnswer) "✅ Perfect Memory!" else "❌ Not quite — remember for tomorrow!",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (selectedAnswer == correctAnswer) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
            Spacer(Modifier.height(16.dp))
            SmritiButton(
                text = "Next Question →",
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )
        }
    }
}

@Composable
private fun ResultsStep(score: Int, total: Int, onFinish: () -> Unit, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp).fillMaxWidth()) {
            Text(if (score >= total * 0.7) "🌟" else "💪", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(16.dp))
            Text("You remembered $score out of $total tasks!", style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text(
                when {
                    score == total -> "Excellent! Your mind is very sharp today."
                    score >= total * 0.6 -> "Great job! Keep practicing every day."
                    else -> "Good effort. Let's try again tomorrow!"
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(40.dp))
            SmritiButton(
                text = "Try Again",
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onFinish) {
                Text("Back to Games Menu", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
