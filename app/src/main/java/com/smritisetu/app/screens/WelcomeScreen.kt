package com.smritisetu.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.SmritiButton

data class LangOption(val label: String, val code: String)

val languages = listOf(
    LangOption("English", "en"),
    LangOption("অসমীया (Assamese)", "as"),
    LangOption("বাংলা (Bengali)", "bn"),
    LangOption("मणिपुरी (Manipuri)", "mni"),
    LangOption("मिज़ो (Mizo)", "lus")
)

@Composable
fun WelcomeScreen(navController: NavController) {
    var selectedLang by remember { mutableStateOf(languages[0]) }
    var showLangMenu by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "logo")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    AnimatedScreen {
        AnimatedGradientBox {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Arogya Smriti",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.graphicsLayer(translationY = floatOffset)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Bridge to Memory — Cognitive Care for NER Elders",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(40.dp))

                // Language selector
                Box {
                    OutlinedButton(
                        onClick = { showLangMenu = true },
                        modifier = Modifier.fillMaxWidth(0.85f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🌐 Language: ${selectedLang.label}")
                    }
                    DropdownMenu(expanded = showLangMenu, onDismissRequest = { showLangMenu = false }) {
                        languages.forEach { lang ->
                            DropdownMenuItem(text = { Text(lang.label) }, onClick = {
                                selectedLang = lang
                                showLangMenu = false
                            })
                        }
                    }
                }

                Spacer(Modifier.height(48.dp))

                SmritiButton(
                    text = "👴 I am the Patient",
                    onClick = { navController.navigate("patient_home") },
                    modifier = Modifier.fillMaxWidth(0.85f).height(72.dp),
                    containerColor = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(20.dp))
                SmritiButton(
                    text = "👨‍👩‍👧 I am a Caregiver",
                    onClick = { navController.navigate("caregiver_dashboard") },
                    modifier = Modifier.fillMaxWidth(0.85f).height(72.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
