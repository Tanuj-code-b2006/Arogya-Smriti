package com.smritisetu.app.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smritisetu.app.R
import com.smritisetu.app.data.LanguagePreference
import com.smritisetu.app.data.Localization
import com.smritisetu.app.data.SupportedLanguages
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton

@Composable
fun WelcomeScreen(navController: NavController) {
    val s = Localization.strings()
    var selectedLang by remember { mutableStateOf(LanguagePreference.selected.value) }
    var showLangMenu by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "logo")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.welcomeColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo with floating animation
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer(translationY = floatOffset)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Fit
                )
                
                Spacer(Modifier.height(24.dp))
                
                Text(
                    text = s.appName,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = s.welcomeMsg,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(Modifier.height(40.dp))

                // Language selector
                Box {
                    OutlinedButton(
                        onClick = { showLangMenu = true },
                        modifier = Modifier.fillMaxWidth(0.85f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🌐 Language: ${selectedLang.label}", fontSize = 18.sp)
                    }
                    DropdownMenu(expanded = showLangMenu, onDismissRequest = { showLangMenu = false }) {
                        SupportedLanguages.list.forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Text(lang.label + if (!lang.bhashiniVoiceSupported) " (voice: soon)" else "")
                                },
                                onClick = {
                                    selectedLang = lang
                                    LanguagePreference.selected.value = lang
                                    showLangMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(48.dp))

                SmritiButton(
                    text = s.iAmPatient,
                    onClick = { navController.navigate("patient_home") },
                    modifier = Modifier.fillMaxWidth(0.85f).height(72.dp),
                    containerColor = MaterialTheme.colorScheme.secondary
                )
                
                Spacer(Modifier.height(20.dp))
                
                SmritiButton(
                    text = s.iAmCaregiver,
                    onClick = { navController.navigate("caregiver_section") },
                    modifier = Modifier.fillMaxWidth(0.85f).height(72.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
