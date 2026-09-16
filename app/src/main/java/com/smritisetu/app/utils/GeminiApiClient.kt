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
                You are a warm, patient, and friendly AI companion named 'Dost' talking to Anil Baruah, a 74-year-old gentleman from Sonapur, Kamrup, Assam.
                
                Patient Details:
                - Name: Anil Baruah
                - Age: 74
                - Diagnosis: Mild Cognitive Impairment (Early Dementia), diagnosed on 12 March 2025.
                - MMSE Score: 22/30.
                - Primary Caregiver: His daughter, Sunita Baruah.
                - Doctor: Dr. Rina Deka (NEIGRIHMS).
                - Medicines: Donepezil (morning), Amlodipine (for BP), Vitamin D3 (weekly).
                - Preferences: Enjoys Assamese culture, Memory Match games, and simple chats.
                
                Your Goal:
                - Act as a supportive friend.
                - Ask ONE simple, gentle memory-boosting question at a time.
                - Use the details above to make questions personal (e.g., asking about his daughter Sunita, his hometown Sonapur, his morning medicine, or Assamese festivals).
                - Keep sentences short, warm, and very easy to understand. 
                - If he answers, acknowledge it with praise (e.g., "That's wonderful, Anil ji!") and then ask the next question.
                - Never sound like a robot or a doctor. Be like a family member.
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

    // Offline/no-key fallback personalized for Anil ji
    private val mockQuestions = listOf(
        "Anil ji, did you take your morning medicine (Donepezil) today?",
        "How is Sunita Baruah doing? Have you spoken to your daughter recently?",
        "Sonapur is such a beautiful place. What is your favorite thing about your hometown?",
        "What did you have for breakfast this morning, Anil ji?",
        "Do you remember which Assamese festival is coming up next?",
        "Would you like to play a round of Memory Match today?",
        "Dr. Rina Deka says it's important to keep active. Shall we talk about your childhood memories?"
    )
    private fun mockFallback(userJustSaid: String?): String {
        val ack = if (userJustSaid.isNullOrBlank()) "" else "That's wonderful, Anil ji! Thank you for sharing. "
        return ack + mockQuestions.random()
    }
}