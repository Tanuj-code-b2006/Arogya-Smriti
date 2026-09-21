package com.smritisetu.app.data

import androidx.compose.runtime.mutableStateOf

data class AppLanguage(
    val label: String,           // Dropdown mein dikhne wala naam
    val bhashiniCode: String,    // Bhashini ka language code
    val localeTag: String,       // Android native STT/TTS fallback ke liye locale
    val bhashiniVoiceSupported: Boolean = true // Bhashini ke paas is bhasha ke ASR/TTS model hain ya nahi
)

object SupportedLanguages {
    val list = listOf(
        AppLanguage("English", "en", "en-IN"),
        AppLanguage("অসমীয়া (Assamese)", "as", "as-IN"),
        AppLanguage("বাংলা (Bengali)", "bn", "bn-IN"),
        AppLanguage("बड़ो (Bodo)", "brx", "hi-IN"),
        AppLanguage("মৈতৈলোন্ (Manipuri)", "mni", "mni-IN"),
        AppLanguage("नेपाली (Nepali)", "ne", "ne-IN"),
        AppLanguage("हिन्दी (Hindi)", "hi", "hi-IN"),
    )
    val default = list[0]
}

/**
 * App-wide selected language — WelcomeScreen isse set karti hai,
 * baaki saari screens (games, chat, reminders) yahin se padhti hain.
 */
object LanguagePreference {
    val selected = mutableStateOf(SupportedLanguages.default)
}