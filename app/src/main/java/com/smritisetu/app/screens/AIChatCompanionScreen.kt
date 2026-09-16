package com.smritisetu.app.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.smritisetu.app.ui.*
import com.smritisetu.app.utils.GeminiApiClient
import com.smritisetu.app.utils.TtsManager
import com.smritisetu.app.utils.VoiceInputManager
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun AIChatCompanionScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember {
        mutableStateOf(
            listOf(ChatMessage("Hello Anil ji! 🙏 I'm your friend, Dost. Ready for a chat?", isUser = false))
        )
    }
    var isThinking by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var voiceEnabled by remember { mutableStateOf(true) }

    val ttsManager = remember { TtsManager(context) }
    val voiceInputManager = remember {
        VoiceInputManager(
            context = context,
            onResult = { spokenText ->
                isListening = false
                if (spokenText.isNotBlank()) {
                    messages = messages + ChatMessage(spokenText, isUser = true)
                    scope.launch {
                        isThinking = true
                        try {
                            val history = messages.map { (if (it.isUser) "user" else "model") to it.text }
                            val reply = GeminiApiClient.getNextMemoryQuestionOrReply(history, spokenText)
                            messages = messages + ChatMessage(reply, isUser = false)
                            if (voiceEnabled) {
                                isSpeaking = true
                                ttsManager.speak(reply) { isSpeaking = false }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        } finally {
                            isThinking = false
                        }
                    }
                }
            },
            onError = { error ->
                isListening = false
                Toast.makeText(context, "Dost didn't catch that: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
            voiceInputManager.destroy()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🗣️ Dost - Talking Friend",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
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
                        TextButton(onClick = { navController.popBackStack() }) { Text("Exit") }
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
                        item { ChatBubble(ChatMessage("Dost is thinking... 🤔", isUser = false)) }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isListening) {
                        PulsingMicAnimation()
                    } else {
                        SmritiButton(
                            text = if (isSpeaking) "Dost is speaking..." else "Tap to Talk to Dost",
                            onClick = {
                                ttsManager.stop()
                                isListening = true
                                voiceInputManager.startListening()
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
