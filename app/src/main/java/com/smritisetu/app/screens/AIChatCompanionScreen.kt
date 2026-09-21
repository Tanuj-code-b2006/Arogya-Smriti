package com.smritisetu.app.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.data.AppLanguage
import com.smritisetu.app.data.LanguagePreference
import com.smritisetu.app.data.Localization
import com.smritisetu.app.ui.*
import com.smritisetu.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun AIChatCompanionScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val appLanguage = LanguagePreference.selected.value
    val s = Localization.strings()

    val ttsManager = remember { TtsManager(context) }
    val recorder = remember { WavAudioRecorder() }
    
    var isThinking by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var hasStarted by remember { mutableStateOf(false) }
    var voiceEnabled by remember { mutableStateOf(true) }
    var isBhashiniActive by remember { mutableStateOf(false) }

    var messages by remember {
        mutableStateOf(listOf(ChatMessage(s.dostInitialMsg, isUser = false)))
    }

    val usingBhashiniConfig = BhashiniApiClient.isConfigured() && appLanguage.bhashiniVoiceSupported

    // Custom Speech Manager (No Google Popup)
    val voiceInputManager = remember {
        VoiceInputManager(
            context = context,
            onResult = { spokenText ->
                isRecording = false
                if (spokenText.isNotBlank()) {
                    handleUserSpeech(spokenText, messages, scope, appLanguage, voiceEnabled, context, ttsManager,
                        onUpdate = { messages = it }, onThinking = { isThinking = it }, onSpeaking = { isSpeaking = it })
                }
            },
            onError = { error ->
                isRecording = false
                isBhashiniActive = false
                Toast.makeText(context, "Dost: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> 
        if (granted) {
            if (usingBhashiniConfig) {
                recorder.start(context)
                isBhashiniActive = true
            } else {
                voiceInputManager.startListening(appLanguage.localeTag)
                isBhashiniActive = false
            }
            isRecording = true
        } else {
            Toast.makeText(context, "Microphone permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
            voiceInputManager.destroy()
        }
    }

    LaunchedEffect(messages.size) { if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1) }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            s.dostTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isBhashiniActive) Color(0xFF2E7D32) else Color(0xFF546E7A),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = if (isBhashiniActive) "via Bhashini 🇮🇳" else "via Native Voice",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "🌐 ${appLanguage.label}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { 
                            voiceEnabled = !voiceEnabled 
                            if (!voiceEnabled) ttsManager.stop()
                        }) {
                            Icon(
                                if (voiceEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                                contentDescription = "Toggle voice",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        TextButton(onClick = {
                            ttsManager.stop()
                            navController.popBackStack()
                        }) { Text(s.exit) }
                    }
                }
                
                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { msg -> ChatBubble(msg) }
                    if (isThinking) {
                        item { ChatBubble(ChatMessage(s.dostThinking, isUser = false)) }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecording) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PulsingMicAnimation()
                            Spacer(Modifier.height(8.dp))
                            Text(s.dostListening, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            
                            Spacer(Modifier.height(8.dp))
                            
                            OutlinedButton(
                                onClick = { 
                                    if (isBhashiniActive) {
                                        isRecording = false
                                        val wavFile = recorder.stop(context)
                                        scope.launch {
                                            isThinking = true
                                            val transcript = wavFile?.let { VoiceService.transcribe(it, appLanguage) }
                                            if (!transcript.isNullOrBlank()) {
                                                handleUserSpeech(transcript, messages, scope, appLanguage, voiceEnabled, context, ttsManager,
                                                    onUpdate = { messages = it }, onThinking = { isThinking = it }, onSpeaking = { isSpeaking = it })
                                            } else {
                                                isThinking = false
                                                isBhashiniActive = false
                                                Toast.makeText(context, "Bhashini missed it", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        voiceInputManager.stopListening()
                                        isRecording = false
                                    }
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(s.stopListening, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    } else {
                        SmritiButton(
                            text = if (!hasStarted) s.startChatting else s.tapToTalk,
                            onClick = {
                                if (!hasStarted) {
                                    beginConversation(scope, appLanguage, voiceEnabled, context, ttsManager, onUpdate = { messages = it }, onThinking = { isThinking = it }, onSpeaking = { isSpeaking = it }, onStarted = { hasStarted = true })
                                } else {
                                    ttsManager.stop()
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(72.dp),
                            enabled = !isThinking && !isSpeaking,
                            icon = { Icon(Icons.Default.Mic, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}

private fun beginConversation(
    scope: CoroutineScope,
    appLanguage: AppLanguage,
    voiceEnabled: Boolean,
    context: Context,
    ttsManager: TtsManager,
    onUpdate: (List<ChatMessage>) -> Unit,
    onThinking: (Boolean) -> Unit,
    onSpeaking: (Boolean) -> Unit,
    onStarted: () -> Unit
) {
    onStarted()
    scope.launch {
        onThinking(true)
        val reply = GeminiApiClient.getNextMemoryQuestionOrReply(emptyList(), null, appLanguage.label)
        onUpdate(listOf(ChatMessage(reply, isUser = false)))
        onThinking(false)
        if (voiceEnabled) {
            onSpeaking(true)
            VoiceService.speak(reply, appLanguage, context, ttsManager) { onSpeaking(false) }
        }
    }
}

private fun handleUserSpeech(
    spokenText: String,
    currentMessages: List<ChatMessage>,
    scope: CoroutineScope,
    appLanguage: AppLanguage,
    voiceEnabled: Boolean,
    context: Context,
    ttsManager: TtsManager,
    onUpdate: (List<ChatMessage>) -> Unit,
    onThinking: (Boolean) -> Unit,
    onSpeaking: (Boolean) -> Unit
) {
    val updated = currentMessages + ChatMessage(spokenText, isUser = true)
    onUpdate(updated)
    scope.launch {
        onThinking(true)
        val history = updated.map { (if (it.isUser) "user" else "model") to it.text }
        val reply = GeminiApiClient.getNextMemoryQuestionOrReply(history, spokenText, appLanguage.label)
        onUpdate(updated + ChatMessage(reply, isUser = false))
        onThinking(false)
        if (voiceEnabled) {
            onSpeaking(true)
            VoiceService.speak(reply, appLanguage, context, ttsManager) { onSpeaking(false) }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (msg.isUser) 16.dp else 2.dp,
                bottomEnd = if (msg.isUser) 2.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = (if (msg.isUser) Color(0xFFDCEDC8) else Color.White).copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                text = msg.text,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (msg.isUser) FontWeight.Normal else FontWeight.Medium
            )
        }
    }
}
