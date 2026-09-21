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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smritisetu.app.R
import com.smritisetu.app.data.AppStrings
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton
import com.smritisetu.app.utils.rememberVoiceNarrator
import com.smritisetu.app.data.Localization

data class QuizQuestion(
    val imageRes: Int,
    val question: String,
    val hint: String,
    val options: List<String>,
    val correctAnswer: String
)

private fun getLocalizedQuestions(s: AppStrings) = listOf(
    QuizQuestion(
        R.drawable.sandesh,
        s.q1,
        s.h1,
        s.q1opts,
        s.q1opts[0] // Sandesh
    ),
    QuizQuestion(
        R.drawable.mekhela_chador,
        s.q2,
        s.h2,
        s.q2opts,
        s.q2opts[1] // Mekhela Chador
    ),
    QuizQuestion(
        R.drawable.mishi_daughter_in_law,
        s.q3,
        s.h3,
        s.q3opts,
        s.q3opts[1] // Daughter-in-law
    ),
    QuizQuestion(
        R.drawable.phanek,
        s.q4,
        s.h4,
        s.q4opts,
        s.q4opts[0] // Phanek
    ),
    QuizQuestion(
        R.drawable.aji_lhamu,
        s.q5,
        s.h5,
        s.q5opts,
        s.q5opts[1] // Aji Lhamu
    ),
    QuizQuestion(
        R.drawable.balush,
        s.q6,
        s.h6,
        s.q6opts,
        s.q6opts[1] // Balushahi
    )
)

@Composable
fun PhotoGuessQuizScreen(navController: NavController) {
    val s = Localization.strings()
    val quizQuestions = remember(s) { getLocalizedQuestions(s) }
    val narrator = rememberVoiceNarrator()
    DisposableEffect(Unit) { onDispose { narrator.stop() } }

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var hintUsed by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        narrator.say(s.photoQuizIntro)
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
                        s.gamePhotoQuiz,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text(s.exit, fontSize = 18.sp)
                    }
                }

                if (!isFinished) {
                    val currentQuestion = quizQuestions[currentIndex]

                    Text(
                        "${s.questionLabel} ${currentIndex + 1} ${s.ofLabel} ${quizQuestions.size}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(Modifier.height(12.dp))

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
                                .height(220.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Image(
                                painter = painterResource(id = quizQuestions[index].imageRes),
                                contentDescription = "Guess this",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    
                    Text(
                        text = currentQuestion.question,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    if (!showHint && !showFeedback) {
                        TextButton(onClick = { 
                            showHint = true 
                            hintUsed = true
                        }) {
                            Text("💡 ${s.hintLabel}", color = MaterialTheme.colorScheme.secondary)
                        }
                    } else if (showHint) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                        ) {
                            Text(
                                text = "${s.hintText}: ${currentQuestion.hint}",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    currentQuestion.options.forEach { option ->
                        val isSelected = selectedOption == option
                        val isCorrect = option == currentQuestion.correctAnswer
                        
                        val buttonColor = when {
                            !showFeedback -> MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            isCorrect -> Color(0xFFC8E6C9)
                            isSelected && !isCorrect -> Color(0xFFFFCDD2)
                            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        }

                        Card(
                            onClick = {
                                if (!showFeedback) {
                                    selectedOption = option
                                    showFeedback = true
                                    if (isCorrect) {
                                        score += if (hintUsed) 3 else 5
                                    }
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
                            text = if (currentIndex < quizQuestions.size - 1) s.next else s.seeResults,
                            onClick = {
                                if (currentIndex < quizQuestions.size - 1) {
                                    currentIndex++
                                    selectedOption = null
                                    showFeedback = false
                                    hintUsed = false
                                    showHint = false
                                } else {
                                    isFinished = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(64.dp)
                        )
                    }

                } else {
                    QuizResultCard(score = score, total = quizQuestions.size * 5, s = s) {
                        navController.popBackStack()
                    }
                }
                
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun QuizResultCard(score: Int, total: Int, s: AppStrings, onBack: () -> Unit) {
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
                    score >= total * 0.8 -> s.perfectMemory
                    score >= total * 0.5 -> s.greatJob
                    else -> s.goodEffort
                },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                s.youScored,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                "$score / $total",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Text(
                s.pointsLabel,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(40.dp))

            SmritiButton(
                text = s.backToGames,
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )
        }
    }
}
