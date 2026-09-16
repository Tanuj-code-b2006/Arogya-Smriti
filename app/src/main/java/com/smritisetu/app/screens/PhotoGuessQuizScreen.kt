package com.smritisetu.app.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.smritisetu.app.ui.SmritiButton

data class QuizQuestion(
    val imageRes: Int,
    val options: List<String>,
    val correctAnswer: String
)

private val quizQuestions = listOf(
    QuizQuestion(
        R.drawable.sandesh,
        listOf("Sandesh", "Rasgulla", "Laddu", "Barfi"),
        "Sandesh"
    ),
    QuizQuestion(
        R.drawable.mekhela_chador,
        listOf("Sari", "Mekhela Chador", "Salwar Kameez", "Lehenga"),
        "Mekhela Chador"
    ),
    QuizQuestion(
        R.drawable.phanek,
        listOf("Phanek", "Lungi", "Dhoti", "Skirt"),
        "Phanek"
    ),
    QuizQuestion(
        R.drawable.aji_lhamu,
        listOf("Bihu Dance", "Aji Lhamu", "Kathak", "Sattriya"),
        "Aji Lhamu"
    ),
    QuizQuestion(
        R.drawable.balush,
        listOf("Jalebi", "Balushahi", "Peda", "Gulab Jamun"),
        "Balushahi"
    )
)

@Composable
fun PhotoGuessQuizScreen(navController: NavController) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

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
                        "🖼️ Pehchano Kaun?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Exit", fontSize = 18.sp)
                    }
                }

                if (!isFinished) {
                    val currentQuestion = quizQuestions[currentIndex]

                    Text(
                        "Question ${currentIndex + 1} of ${quizQuestions.size}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    AnimatedContent(
                        targetState = currentIndex,
                        transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(300)) },
                        label = "questionTransition"
                    ) { index ->
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Image(
                                painter = painterResource(id = quizQuestions[index].imageRes),
                                contentDescription = "Guess this",
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    quizQuestions[currentIndex].options.forEach { option ->
                        val isSelected = selectedOption == option
                        val isCorrect = option == currentQuestion.correctAnswer
                        
                        val buttonColor = when {
                            !showFeedback -> MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            isCorrect -> Color(0xFFC8E6C9) // Green for correct
                            isSelected && !isCorrect -> Color(0xFFFFCDD2) // Red for wrong
                            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
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
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = buttonColor),
                            elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp)
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (showFeedback) {
                        Spacer(Modifier.height(24.dp))
                        SmritiButton(
                            text = if (currentIndex < quizQuestions.size - 1) "Next →" else "See Results",
                            onClick = {
                                if (currentIndex < quizQuestions.size - 1) {
                                    currentIndex++
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
                    QuizResultCard(score = score, total = quizQuestions.size) {
                        navController.popBackStack()
                    }
                }
                
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun QuizResultCard(score: Int, total: Int, onBack: () -> Unit) {
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
                text = when {
                    score == total -> "🏆 Perfect Score!"
                    score >= total / 2 -> "🌟 Great Job!"
                    else -> "💪 Good Effort!"
                },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                "You identified",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                "$score out of $total",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Text(
                "correctly!",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(40.dp))

            SmritiButton(
                text = "Back to Mind Games",
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )
        }
    }
}
