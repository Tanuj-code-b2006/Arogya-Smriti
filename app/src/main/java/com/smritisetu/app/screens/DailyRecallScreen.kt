package com.smritisetu.app.screens

import androidx.compose.foundation.layout.*
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

@Composable
fun DailyRecallScreen(navController: NavController) {
    val s = Localization.strings()
    val narrator = rememberVoiceNarrator()
    
    LaunchedEffect(Unit) {
        narrator.say(s.dailyRecallIntro)
    }
    DisposableEffect(Unit) { onDispose { narrator.stop() } }

    var morningText by remember { mutableStateOf("") }
    var afternoonText by remember { mutableStateOf("") }
    var eveningText by remember { mutableStateOf("") }

    var morningForgot by remember { mutableStateOf(false) }
    var afternoonForgot by remember { mutableStateOf(false) }
    var eveningForgot by remember { mutableStateOf(false) }

    var showResult by remember { mutableStateOf(false) }

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
                        s.todayMemories,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text(s.back) }
                }
                Text(
                    text = s.tryRememberDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(Modifier.height(24.dp))

                RecallField(
                    label = "☀️ ${s.morning}...",
                    text = morningText,
                    onValueChange = { morningText = it; morningForgot = false },
                    isForgot = morningForgot,
                    onForgotToggle = { morningForgot = !morningForgot; if (morningForgot) morningText = "" },
                    s = s
                )

                Spacer(Modifier.height(20.dp))

                RecallField(
                    label = "🌤️ ${s.afternoon}...",
                    text = afternoonText,
                    onValueChange = { afternoonText = it; afternoonForgot = false },
                    isForgot = afternoonForgot,
                    onForgotToggle = { afternoonForgot = !afternoonForgot; if (afternoonForgot) afternoonText = "" },
                    s = s
                )

                Spacer(Modifier.height(20.dp))

                RecallField(
                    label = "🌙 ${s.evening}...",
                    text = eveningText,
                    onValueChange = { eveningText = it; eveningForgot = false },
                    isForgot = eveningForgot,
                    onForgotToggle = { eveningForgot = !eveningForgot; if (eveningForgot) eveningText = "" },
                    s = s
                )

                Spacer(Modifier.height(32.dp))

                if (!showResult) {
                    SmritiButton(
                        text = s.doneForToday,
                        onClick = { showResult = true },
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )
                } else {
                    val answeredCount = listOf(
                        morningText.isNotBlank(),
                        afternoonText.isNotBlank(),
                        eveningText.isNotBlank()
                    ).count { it }
                    
                    val points = answeredCount * 10
                    
                    RecallResultCard(answeredCount, points, s) {
                        navController.popBackStack()
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RecallField(
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
    isForgot: Boolean,
    onForgotToggle: () -> Unit,
    s: AppStrings
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = onForgotToggle) {
                    Text(if (isForgot) s.iRemember else s.iForgot, color = MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(Modifier.height(8.dp))
            if (!isForgot) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onValueChange,
                    placeholder = { Text(s.whatDidYouDo) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                Text(
                    text = s.noWorriesMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun RecallResultCard(count: Int, points: Int, s: AppStrings, onDismiss: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9).copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (count) {
                    3 -> s.resFantastic
                    2 -> s.resGreat
                    1 -> s.resWellDone
                    else -> s.resKeepTrying
                },
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = when (count) {
                    3 -> s.msgAll
                    2 -> s.msgMost
                    1 -> s.msgSome
                    else -> s.msgNone
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "${s.earnedPoints} $points ${s.pointsShort}!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF6C00)
            )
            Spacer(Modifier.height(24.dp))
            SmritiButton(
                text = s.seeYouLater,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
