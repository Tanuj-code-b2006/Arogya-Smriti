package com.smritisetu.app.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.SmritiButton
import com.smritisetu.app.utils.GeminiApiClient
import com.smritisetu.app.utils.TtsManager
import kotlinx.coroutines.launch
import java.util.Locale

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun AIChatCompanionScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // TTS instance, tied to this screen's lifecycle
    val ttsManager = remember { TtsManager(context) }
    DisposableEffect(Unit) {
        onDispose { ttsManager.shutdown() }
    }

    var messages by remember {
        mutableStateOf(
            listOf(ChatMessage("Hello Anil ji! 🙏 I'm your memory companion. Ready for a little chat?", isUser = false))
        )
    }
    var isThinking by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var hasStarted by remember { mutableStateOf(false) }
    var voiceEnabled by remember { mutableStateOf(true) }

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
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
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your answer...")
            }
            isListening = true
            speechLauncher.launch(intent)
        } else {
            Toast.makeText(context, "Microphone permission needed for voice chat", Toast.LENGTH_SHORT).show()
        }
    }

    fun startListening() {
        Toast.makeText(context, "Listening now — please speak...", Toast.LENGTH_SHORT).show()
        micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    fun beginConversation() {
        hasStarted = true
        scope.launch {
            isThinking = true
            try {
                val reply = GeminiApiClient.getNextMemoryQuestionOrReply(emptyList(), null)
                messages = messages + ChatMessage(reply, isUser = false)
                if (voiceEnabled) {
                    isSpeaking = true
                    ttsManager.speak(reply) { isSpeaking = false }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error starting conversation: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                isThinking = false
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    AnimatedScreen {
        AnimatedGradientBox {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🗣️ Memory Companion",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
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
                        TextButton(onClick = {
                            ttsManager.stop()
                            navController.popBackStack()
                        }) { Text("Exit") }
                    }
                }
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg -> ChatBubble(msg) }
                    if (isThinking) {
                        item { ChatBubble(ChatMessage("Thinking... 🤔", isUser = false)) }
                    }
                    if (isSpeaking) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(6.dp))
                                Text("Speaking...", style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                SmritiButton(
                    text = when {
                        !hasStarted -> "Start Chatting"
                        isSpeaking -> "AI is speaking..."
                        isListening -> "Listening..."
                        else -> "Tap to Speak Your Answer"
                    },
                    onClick = {
                        if (!hasStarted) beginConversation() else startListening()
                    },
                    modifier = Modifier.fillMaxWidth().height(72.dp),
                    containerColor = if (isListening) Color(0xFFC62828) else MaterialTheme.colorScheme.primary,
                    enabled = !isThinking && !isSpeaking,
                    icon = {
                        Icon(Icons.Filled.Mic, contentDescription = "Speak", modifier = Modifier.size(28.dp))
                    }
                )
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
                containerColor = (if (msg.isUser) Color(0xFFDCEDC8) else Color(0xFFFFFFFF)).copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Text(
                text = msg.text,
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}