package com.smritisetu.app.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import com.smritisetu.app.data.AppLanguage
import java.io.File
import java.io.FileOutputStream

object VoiceService {

    /** Speaks `text` in the given language — Bhashini TTS if available & supported, else native Android TTS. */
    suspend fun speak(
        text: String,
        lang: AppLanguage,
        context: Context,
        ttsManager: TtsManager,
        onDone: () -> Unit
    ) {
        val useBhashini = BhashiniApiClient.isConfigured() && lang.bhashiniVoiceSupported && lang.bhashiniCode != "en"
        val audioBase64 = if (useBhashini) BhashiniApiClient.textToSpeech(text, lang.bhashiniCode) else null

        if (audioBase64 != null) {
            playBase64Audio(audioBase64, context, onDone)
        } else {
            ttsManager.speak(text, onDone) // graceful fallback — demo never breaks
        }
    }

    /** Transcribes recorded `wavFile` in the given language — Bhashini ASR if available, else null (caller falls back). */
    suspend fun transcribe(wavFile: File, lang: AppLanguage): String? {
        if (!BhashiniApiClient.isConfigured() || !lang.bhashiniVoiceSupported) return null
        val base64 = Base64.encodeToString(wavFile.readBytes(), Base64.NO_WRAP)
        return BhashiniApiClient.speechToText(base64, lang.bhashiniCode)
    }

    private fun playBase64Audio(base64: String, context: Context, onDone: () -> Unit) {
        try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val file = File(context.cacheDir, "bhashini_tts_${System.currentTimeMillis()}.wav")
            FileOutputStream(file).use { it.write(bytes) }
            val player = MediaPlayer()
            player.setDataSource(file.absolutePath)
            player.setOnCompletionListener {
                it.release()
                file.delete()
                onDone()
            }
            player.prepare()
            player.start()
        } catch (e: Exception) {
            onDone()
        }
    }
}