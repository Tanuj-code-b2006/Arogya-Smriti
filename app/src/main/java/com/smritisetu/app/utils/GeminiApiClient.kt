package com.smritisetu.app.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object GeminiApiClient {

    // 🔑 PASTE YOUR FREE GEMINI API KEY HERE (from https://aistudio.google.com/apikey)
    private const val API_KEY = "AIzaSyDxLUlCy7_iP0czN6FmAfIVkDNBD-jdP5A"

    private const val ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=$API_KEY"

    private val client = OkHttpClient()

    /**
     * Sends the conversation so far + a system instruction to Gemini and returns
     * the model's next question/response as a String.
     * Falls back to a local mock question bank if no API key is set or the call fails,
     * so the demo NEVER breaks on stage even without internet/API key.
     */
    suspend fun getNextMemoryQuestionOrReply(
        conversationHistory: List<Pair<String, String>>, // (role "user"/"model", text)
        userJustSaid: String?
    ): String = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = """
                You are a warm, patient companion talking to an elderly dementia patient in North-East India.
                Ask ONE simple, gentle memory-boosting question at a time (e.g. about their day, family,
                childhood, food, festivals). Keep sentences short and easy to understand. Never sound clinical.
                If the user answers, briefly praise/acknowledge their answer, then ask the next simple question.
            """.trimIndent()

            val contents = JSONArray()
            contents.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
            })
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

            val body = JSONObject().put("contents", contents)
                .toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder().url(ENDPOINT).post(body).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext mockFallback(userJustSaid)
                val json = JSONObject(response.body?.string() ?: return@withContext mockFallback(userJustSaid))
                json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            }
        } catch (e: IOException) {
            mockFallback(userJustSaid)
        } catch (e: Exception) {
            mockFallback(userJustSaid)
        }
    }

    // Offline/no-key fallback so the demo always works
    private val mockQuestions = listOf(
        "What did you eat for breakfast today?",
        "Can you tell me the name of your favorite festival?",
        "Who is your youngest grandchild? What is their name?",
        "What was your favorite game to play as a child?",
        "What is the name of the village or town you were born in?",
        "Can you describe what the weather is like today?"
    )
    private fun mockFallback(userJustSaid: String?): String {
        val ack = if (userJustSaid.isNullOrBlank()) "" else "That's wonderful, thank you for sharing! "
        return ack + mockQuestions.random()
    }
}