package com.smritisetu.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.smritisetu.app.data.LanguagePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Drop this into any screen to get one-line voice narration in the
 * patient's selected language (Bhashini if configured & supported,
 * else native Android TTS as automatic fallback).
 *
 * Usage:
 *   val narrator = rememberVoiceNarrator()
 *   narrator.say("Tap every apple you see!")
 */
class VoiceNarrator internal constructor(
    private val ttsManager: TtsManager,
    private val context: android.content.Context,
    private val scope: CoroutineScope
) {
    fun say(text: String, onDone: () -> Unit = {}) {
        val lang = LanguagePreference.selected.value
        scope.launch {
            VoiceService.speak(text, lang, context, ttsManager, onDone)
        }
    }

    fun stop() = ttsManager.stop()
}

@Composable
fun rememberVoiceNarrator(): VoiceNarrator {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val ttsManager = remember { TtsManager(context) }
    DisposableEffect(Unit) { onDispose { ttsManager.shutdown() } }
    return remember { VoiceNarrator(ttsManager, context, scope) }
}