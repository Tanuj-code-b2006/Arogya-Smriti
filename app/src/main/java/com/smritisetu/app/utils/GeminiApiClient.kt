package com.smritisetu.app.utils

import com.smritisetu.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object GeminiApiClient {

    // Accessing API Key from BuildConfig (securely stored in local.properties)
    private val API_KEY = BuildConfig.GEMINI_API_KEY

    private val ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$API_KEY"

    private val client = OkHttpClient()

    suspend fun getNextMemoryQuestionOrReply(
        conversationHistory: List<Pair<String, String>>,
        userJustSaid: String?,
        languageLabel: String = "English"
    ): String = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = """
                You are a warm, patient, and friendly AI companion named 'Dost' talking to Anil Baruah, a 74-year-old gentleman from Sonapur, Kamrup, Assam.
                
                Patient Details:
                - Name: Anil Baruah
                - Age: 74
                - Diagnosis: Mild Cognitive Impairment (Early Dementia)
                - Primary Caregiver: His daughter, Sunita Baruah
                - Doctor: Dr. Rina Deka (NEIGRIHMS)
                - Medicines: Donepezil (morning), Amlodipine (for BP)
                - Preferences: Enjoys Assamese culture, Memory Match games.
                
                CRITICAL INSTRUCTION:
                - You MUST respond ONLY in the $languageLabel language.
                - Use the script (alphabet) of $languageLabel.
                - Even if the conversation history is in English, your next response MUST be in $languageLabel.
                - If the language is 'Assamese', write in Assamese script.
                - If the language is 'Hindi', write in Devanagari script.
                - Act as a supportive friend. Do not sound like a machine.
                - Ask ONE simple, personal question at a time.
                - IMPORTANT: Do not use any English words if the target language is different.
            """.trimIndent()

            val contents = JSONArray()
            conversationHistory.forEach { (role, text) ->
                contents.put(JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().put(JSONObject().put("text", text)))
                })
            }
            if (userJustSaid != null) {
                contents.put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", userJustSaid)))
                })
            }

            val requestBodyJson = JSONObject().apply {
                put("contents", contents)
                put("system_instruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                })
            }

            val body = requestBodyJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(ENDPOINT).post(body).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext mockFallback(userJustSaid, languageLabel)
                val json = JSONObject(response.body?.string() ?: return@withContext mockFallback(userJustSaid, languageLabel))
                json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            }
        } catch (e: Exception) {
            mockFallback(userJustSaid, languageLabel)
        }
    }

    private fun mockFallback(userJustSaid: String?, lang: String): String {
        return when {
            lang.contains("Hindi") || lang.contains("हिन्दी") -> "नमस्ते अनिल जी, क्या आपने आज अपनी दवाई ली?"
            lang.contains("Assamese") || lang.contains("অসমীয়া") -> "নমস্কাৰ অনিল ডাঙৰীয়া, আপুনি আজি পুৱাৰ দৰব খালে নে?"
            else -> "Hello Anil ji, did you take your morning medicine today?"
        }
    }
}
